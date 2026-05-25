package com.aguiabranca.inovacao.domain.models

enum class UserRole {
    ADMIN_TI,
    LIDERANCA,
    GESTOR,
    OPERADOR
}

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = UserRole.OPERADOR.name,
    val profilePictureUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = 0L,
    val isActive: Boolean = true
) {
    constructor() : this(
        uid = "",
        email = "",
        name = "",
        role = UserRole.OPERADOR.name,
        profilePictureUrl = null
    )
}

data class CurrentUser(
    val uid: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val profilePictureUrl: String?,
    val isActive: Boolean
)

fun String.toUserRoleOrDefault(): UserRole {
    return UserRole.entries.firstOrNull { it.name == this } ?: UserRole.OPERADOR
}

