package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.local.dao.UserDao
import com.aguiabranca.inovacao.data.local.toEntity
import com.aguiabranca.inovacao.data.local.toModel
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.LoginRequest
import com.aguiabranca.inovacao.domain.repository.AuthRepository
import com.aguiabranca.inovacao.domain.models.CurrentUser
import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.models.toUserRoleOrDefault
import com.aguiabranca.inovacao.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase,
    private val userDao: UserDao
) : AuthRepository {
    private val usersRef = firebaseDb.reference.child("users")

    override suspend fun signIn(request: LoginRequest): AppResult<CurrentUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(request.email, request.password).await()
            val uid = authResult.user?.uid.orEmpty()
            val user = try {
                fetchUser(uid)
            } catch (e: Exception) {
                // Recover dangling accounts from a previously failed database insertion
                val pendingSnapshot = firebaseDb.reference.child("pendingUsers").child(request.email.safeEmailKey()).get().await()
                val pendingUser = pendingSnapshot.getValue(User::class.java)
                if (pendingUser != null) {
                    val recoveredUser = pendingUser.copy(uid = uid, email = request.email.trim().lowercase(), createdAt = System.currentTimeMillis())
                    usersRef.child(uid).setValue(recoveredUser).await()
                    runCatching { pendingSnapshot.ref.removeValue().await() }
                    recoveredUser
                } else {
                    throw e
                }
            }
            if (!user.isActive) {
                firebaseAuth.signOut()
                return AppResult.Error("Usuário inativo. Procure o Administrador de TI.")
            }
            userDao.upsert(user.toEntity())
            AppResult.Success(user.toCurrentUser())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível entrar.", e)
        }
    }

    override suspend fun completeFirstAccess(request: LoginRequest): AppResult<CurrentUser> {
        return try {
            if (request.password != BuildConfig.DEFAULT_PASSWORD) {
                return AppResult.Error("Para o primeiro acesso, utilize a senha padrão (${BuildConfig.DEFAULT_PASSWORD}).")
            }

            val authResult = firebaseAuth.createUserWithEmailAndPassword(request.email, request.password).await()
            val uid = authResult.user?.uid.orEmpty()
            val pendingSnapshot = firebaseDb.reference.child("pendingUsers").child(request.email.safeEmailKey()).get().await()
            val pendingUser = pendingSnapshot.getValue(User::class.java)
                ?: return AppResult.Error("Usuário não autorizado. Peça ao Administrador de TI para liberar seu e-mail.")
            val user = pendingUser.copy(uid = uid, email = request.email.trim().lowercase(), createdAt = System.currentTimeMillis())
            usersRef.child(uid).setValue(user).await()
            runCatching { pendingSnapshot.ref.removeValue().await() }
            userDao.upsert(user.toEntity())
            AppResult.Success(user.toCurrentUser())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível completar o primeiro acesso.", e)
        }
    }

    override suspend fun signOut(): AppResult<Unit> {
        firebaseAuth.signOut()
        return AppResult.Success(Unit)
    }

    override suspend fun getCurrentUser(): AppResult<CurrentUser?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return AppResult.Success(null)
            val user = runCatching { fetchUser(firebaseUser.uid) }
                .getOrElse { userDao.getById(firebaseUser.uid)?.toModel() }
            AppResult.Success(user?.toCurrentUser())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível carregar a sessão.", e)
        }
    }

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    private suspend fun fetchUser(uid: String): User {
        val snapshot = usersRef.child(uid).get().await()
        return snapshot.getValue(User::class.java) ?: error("Perfil de usuário não encontrado.")
    }
}

fun String.safeEmailKey(): String = trim().lowercase().replace(".", ",")

private fun User.toCurrentUser(): CurrentUser {
    return CurrentUser(
        uid = uid,
        email = email,
        name = name,
        role = role.toUserRoleOrDefault(),
        profilePictureUrl = profilePictureUrl,
        isActive = isActive
    )
}