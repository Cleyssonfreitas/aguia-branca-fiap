package com.aguiabranca.inovacao.domain

import com.aguiabranca.inovacao.domain.models.CurrentUser
import com.aguiabranca.inovacao.domain.models.UserRole

object Permissions {
    fun canManageUsers(user: CurrentUser?) = user.hasRole(UserRole.ADMIN_TI)
    fun canManageStrategies(user: CurrentUser?) = user.hasRole(UserRole.LIDERANCA)
    fun canReviewIdeas(user: CurrentUser?) = user.hasRole(UserRole.GESTOR)
    fun canManageProjects(user: CurrentUser?) = user.hasRole(UserRole.GESTOR)
    fun canViewDashboard(user: CurrentUser?) = user.hasAnyRole(UserRole.LIDERANCA, UserRole.ADMIN_TI)

    private fun CurrentUser?.hasRole(role: UserRole): Boolean {
        return this?.isActive == true && this.role == role
    }

    private fun CurrentUser?.hasAnyRole(vararg roles: UserRole): Boolean {
        return this?.isActive == true && roles.contains(this.role)
    }
}

