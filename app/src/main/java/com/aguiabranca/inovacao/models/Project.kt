package com.aguiabranca.inovacao.models

enum class ProjectStatus {
    PLANEJAMENTO, APROVADO, EM_PROGRESSO, PAUSADO, CONCLUIDO, CANCELADO
}

data class Project(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val stage: String = "Planejamento",
    val status: String = ProjectStatus.PLANEJAMENTO.name,
    val createdBy: String = "",
    val owner: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val investment: Double = 0.0,
    val roi: Double = 0.0,
    val profit: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val progress: Int = 0,
    val relatedIdeas: List<String> = emptyList(),
    val team: List<String> = emptyList()
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        createdBy = ""
    )
}
