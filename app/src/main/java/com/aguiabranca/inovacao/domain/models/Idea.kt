package com.aguiabranca.inovacao.domain.models

enum class IdeaStatus {
    RASCUNHO, SUBMETIDA, APROVADA, REJEITADA, EM_PROGRESSO, CONCLUIDA
}

enum class IdeaType {
    PROBLEMA, OPORTUNIDADE, MELHORIA
}

data class Idea(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = IdeaType.PROBLEMA.name,
    val status: String = IdeaStatus.SUBMETIDA.name,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val priority: Int = 0,
    val estimatedImpact: String = "",
    val department: String = "",
    val approvedBy: String? = null,
    val rejectionReason: String? = null,
    val views: Int = 0,
    val aiScore: Int? = null,
    val aiFeedback: String? = null
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        type = IdeaType.PROBLEMA.name,
        status = IdeaStatus.SUBMETIDA.name,
        createdBy = ""
    )
}
