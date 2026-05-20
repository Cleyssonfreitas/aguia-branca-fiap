package com.aguiabranca.inovacao.models

import com.google.firebase.database.PropertyName

enum class UserRole {
    OPERADOR, GESTOR, LIDERANCA
}

data class User(
    @PropertyName("uid")
    val uid: String = "",

    @PropertyName("email")
    val email: String = "",

    @PropertyName("name")
    val name: String = "",

    @PropertyName("role")
    val role: String = UserRole.OPERADOR.name,

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("lastLogin")
    val lastLogin: Long = 0L,

    @PropertyName("isActive")
    val isActive: Boolean = true
) {
    constructor() : this(
        uid = "",
        email = "",
        name = "",
        role = UserRole.OPERADOR.name
    )
}

