package com.aguiabranca.inovacao.models

import com.google.firebase.database.PropertyName

data class Strategy(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("title")
    val title: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("createdBy")
    val createdBy: String = "",

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),

    @PropertyName("isActive")
    val isActive: Boolean = true,

    @PropertyName("priority")
    val priority: Int = 1
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        createdBy = ""
    )
}

