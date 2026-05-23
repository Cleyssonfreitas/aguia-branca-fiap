package com.aguiabranca.inovacao.models

import com.google.firebase.database.PropertyName

enum class IdeaStatus {
    ENVIADA, SUBMETIDA, APROVADA, REJEITADA, EM_PROGRESSO, CONCLUIDA
}

enum class IdeaType {
    PROBLEMA, OPORTUNIDADE, MELHORIA
}

data class Idea(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("title")
    val title: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("type")
    val type: String = IdeaType.PROBLEMA.name,

    @PropertyName("status")
    val status: IdeaStatus = IdeaStatus.ENVIADA,

    @PropertyName("createdBy")
    val createdBy: String = "",

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),

    @PropertyName("priority")
    val priority: Int = 0,

    @PropertyName("estimatedImpact")
    val estimatedImpact: String = "",

    @PropertyName("department")
    val department: String = "",

    @PropertyName("approvedBy")
    val approvedBy: String? = null,

    @PropertyName("rejectionReason")
    val rejectionReason: String? = null,

    @PropertyName("views")
    val views: Int = 0
)

