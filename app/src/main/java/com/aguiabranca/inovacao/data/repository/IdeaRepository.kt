package com.aguiabranca.inovacao.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aguiabranca.inovacao.models.Idea
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IdeaRepository(
    private val firebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance()
) {

    private val ideasRef = firebaseDb.reference.child("ideas")

    suspend fun createIdea(idea: Idea): AuthResult<String> {
        return try {
            val newId = ideasRef.push().key ?: return AuthResult.Error(
                Exception("Erro ao gerar ID da ideia")
            )

            val ideaWithId = idea.copy(id = newId)

            suspendCancellableCoroutine { continuation ->
                ideasRef.child(newId).setValue(ideaWithId)
                    .addOnSuccessListener {
                        Log.d("IdeaRepository", "Ideia criada: $newId")
                        continuation.resume(newId)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("IdeaRepository", "Erro ao criar ideia: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(newId)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro na criação: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun getIdeaById(ideaId: String): AuthResult<Idea?> {
        return try {
            val idea = suspendCancellableCoroutine<Idea?> { continuation ->
                ideasRef.child(ideaId).get()
                    .addOnSuccessListener { snapshot ->
                        val idea = snapshot.getValue(Idea::class.java)
                        continuation.resume(idea)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("IdeaRepository", "Erro ao buscar ideia: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(idea)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro: ${e.message}")
            AuthResult.Error(e)
        }
    }

    fun getAllIdeasLiveData(): LiveData<List<Idea>> {
        return MutableLiveData<List<Idea>>().apply {
            ideasRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ideas = mutableListOf<Idea>()
                    snapshot.children.forEach { child ->
                        val idea = child.getValue(Idea::class.java)
                        if (idea != null) {
                            ideas.add(idea)
                        }
                    }
                    postValue(ideas)
                    Log.d("IdeaRepository", "Ideias atualizadas: ${ideas.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("IdeaRepository", "Erro ao buscar ideias: ${error.message}")
                }
            })
        }
    }

    suspend fun getIdeasByUser(userId: String): AuthResult<List<Idea>> {
        return try {
            val ideas = suspendCancellableCoroutine<List<Idea>> { continuation ->
                ideasRef.orderByChild("createdBy").equalTo(userId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val ideasList = mutableListOf<Idea>()
                        snapshot.children.forEach { child ->
                            val idea = child.getValue(Idea::class.java)
                            if (idea != null) {
                                ideasList.add(idea)
                            }
                        }
                        continuation.resume(ideasList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("IdeaRepository", "Erro: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(ideas)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun updateIdea(ideaId: String, idea: Idea): AuthResult<Unit> {
        return try {
            val updatedIdea = idea.copy(updatedAt = System.currentTimeMillis())

            suspendCancellableCoroutine { continuation ->
                ideasRef.child(ideaId).setValue(updatedIdea)
                    .addOnSuccessListener {
                        Log.d("IdeaRepository", "Ideia atualizada: $ideaId")
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("IdeaRepository", "Erro ao atualizar: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun approveIdea(ideaId: String, approvedBy: String): AuthResult<Unit> {
        return try {
            suspendCancellableCoroutine { continuation ->
                ideasRef.child(ideaId).child("status").setValue("APROVADA")
                    .addOnSuccessListener {
                        ideasRef.child(ideaId).child("approvedBy").setValue(approvedBy)
                            .addOnSuccessListener {
                                Log.d("IdeaRepository", "Ideia aprovada: $ideaId")
                                continuation.resume(Unit)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun rejectIdea(ideaId: String, reason: String): AuthResult<Unit> {
        return try {
            suspendCancellableCoroutine { continuation ->
                ideasRef.child(ideaId).child("status").setValue("REJEITADA")
                    .addOnSuccessListener {
                        ideasRef.child(ideaId).child("rejectionReason").setValue(reason)
                            .addOnSuccessListener {
                                Log.d("IdeaRepository", "Ideia rejeitada: $ideaId")
                                continuation.resume(Unit)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro: ${e.message}")
            AuthResult.Error(e)
        }
    }

    suspend fun deleteIdea(ideaId: String): AuthResult<Unit> {
        return try {
            suspendCancellableCoroutine { continuation ->
                ideasRef.child(ideaId).removeValue()
                    .addOnSuccessListener {
                        Log.d("IdeaRepository", "Ideia deletada: $ideaId")
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("IdeaRepository", "Erro ao deletar: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("IdeaRepository", "Erro: ${e.message}")
            AuthResult.Error(e)
        }
    }
}

