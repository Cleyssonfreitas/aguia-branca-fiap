package com.aguiabranca.inovacao.models

import com.google.firebase.database.PropertyName

enum class ProjectStatus {
    PLANEJAMENTO, APROVADO, EM_PROGRESSO, PAUSADO, CONCLUIDO, CANCELADO
}

data class Project(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("title")
    val title: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("status")
    val status: String = ProjectStatus.PLANEJAMENTO.name,

    @PropertyName("createdBy")
    val createdBy: String = "",

    @PropertyName("owner")
    val owner: String = "",

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),

    @PropertyName("startDate")
    val startDate: Long = 0L,

    @PropertyName("endDate")
    val endDate: Long = 0L,

    @PropertyName("investment")
    val investment: Double = 0.0,

    @PropertyName("roi")
    val roi: Double = 0.0,

    @PropertyName("costReduction")
    val costReduction: Double = 0.0,

    @PropertyName("productivityGain")
    val productivityGain: Double = 0.0,

    @PropertyName("progress")
    val progress: Int = 0,

    @PropertyName("relatedIdeas")
    val relatedIdeas: List<String> = emptyList(),

    @PropertyName("team")
    val team: List<String> = emptyList()
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        createdBy = ""
    )
}

