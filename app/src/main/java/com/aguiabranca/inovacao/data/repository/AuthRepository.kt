package com.aguiabranca.inovacao.data.repository

import android.util.Log
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException



class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance()
) {

    private val usersRef = firebaseDb.reference.child("users")

    suspend fun signIn(email: String, password: String): AuthResult<User> {
        return try {
            val authResult = suspendCancellableCoroutine<User> { continuation ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: ""
                        fetchUserData(uid) { user ->
                            if (user != null) {
                                continuation.resume(user)
                            } else {
                                continuation.resumeWithException(
                                    Exception("Dados do usuário não encontrados")
                                )
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(authResult)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no signIn: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        role: UserRole = UserRole.OPERADOR
    ): AuthResult<User> {
        return try {
            val authResult = suspendCancellableCoroutine<User> { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: ""
                        val newUser = User(
                            uid = uid,
                            email = email,
                            name = name,
                            role = role.name,
                            createdAt = System.currentTimeMillis()
                        )

                        usersRef.child(uid).setValue(newUser)
                            .addOnSuccessListener {
                                Log.d("AuthRepository", "Usuário criado: $uid")
                                continuation.resume(newUser)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("AuthRepository", "Erro ao salvar dados: ${exception.message}")
                                continuation.resumeWithException(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("AuthRepository", "Erro no signUp: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(authResult)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro na criação do usuário: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun signOut(): AuthResult<Unit> {
        return try {
            firebaseAuth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no signOut: ${e.message}")
            AuthResult.Error(e)
        }
    }

    private fun fetchUserData(uid: String, callback: (User?) -> Unit) {
        usersRef.child(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "Erro ao buscar dados do usuário: ${exception.message}")
                callback(null)
            }
    }

    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: ""
        )
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun updateUserData(user: User): AuthResult<Unit> {
        return try {
            suspendCancellableCoroutine { continuation ->
                usersRef.child(user.uid).setValue(user)
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao atualizar usuário: ${e.message}")
            AuthResult.Error(e)
        }
    }
}

