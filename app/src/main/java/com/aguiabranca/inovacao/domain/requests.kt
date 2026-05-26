package com.aguiabranca.inovacao.domain

import com.aguiabranca.inovacao.domain.models.UserRole

data class LoginRequest(
    val email: String,
    val password: String
)

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole
)

data class UpdateUserRoleRequest(
    val uid: String,
    val role: UserRole
)

data class SetUserActiveRequest(
    val uid: String,
    val isActive: Boolean
)

data class CreateIdeaRequest(
    val title: String,
    val description: String,
    val type: String,
    val estimatedImpact: String,
    val department: String,
    val aiScore: Int? = null,
    val aiFeedback: String? = null
)

data class ReviewIdeaRequest(
    val ideaId: String,
    val priority: Int? = null,
    val approved: Boolean? = null,
    val rejectionReason: String? = null
)

data class SaveProjectRequest(
    val id: String = "",
    val title: String,
    val description: String,
    val stage: String,
    val status: String,
    val owner: String,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val investment: Double = 0.0,
    val roi: Double = 0.0,
    val profit: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val progress: Int = 0,
    val relatedIdeas: List<String> = emptyList()
)

data class SaveStrategyRequest(
    val id: String = "",
    val title: String,
    val description: String,
    val priority: Int = 1,
    val isActive: Boolean = true
)

