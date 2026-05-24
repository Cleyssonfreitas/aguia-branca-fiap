package com.aguiabranca.inovacao.domain.models

data class Strategy(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val priority: Int = 1
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        createdBy = ""
    )
}