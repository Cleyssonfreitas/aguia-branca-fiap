package com.aguiabranca.inovacao.domain.usecase

import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.models.UserRole

object PermissionUseCase {
    
    fun canApproveIdea(user: User?): Boolean {
        return user != null && user.role == UserRole.GESTOR.name
    }

    fun canCreateProject(user: User?): Boolean {
        return user != null && user.role == UserRole.GESTOR.name
    }

    fun canEditProject(user: User?): Boolean {
        return user != null && (user.role == UserRole.GESTOR.name || user.role == UserRole.LIDERANCA.name)
    }

    fun canAccessDashboard(user: User?): Boolean {
        return user != null && user.role == UserRole.LIDERANCA.name
    }

    fun canManageStrategies(user: User?): Boolean {
        return user != null && user.role == UserRole.LIDERANCA.name
    }

    fun canCreateIdea(user: User?): Boolean {
        return user != null && user.role == UserRole.OPERADOR.name
    }
}

