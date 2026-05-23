package com.aguiabranca.inovacao.domain.usecase

object ValidationUseCase {
    
    fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return email.contains("@") && email.contains(".")
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateName(name: String): Boolean {
        if (name.isBlank()) return false
        return name.length >= 3
    }

    fun validateIdeaCreation(title: String, description: String): Boolean {
        if (title.isBlank() || description.isBlank()) return false
        if (title.length < 5 || description.length < 10) return false
        return true
    }

    fun validateProjectCreation(
        title: String,
        description: String,
        investment: Double,
        expectedReturn: Double
    ): Boolean {
        if (title.isBlank() || description.isBlank()) return false
        if (title.length < 5 || description.length < 10) return false
        if (investment <= 0 || expectedReturn < 0) return false
        return true
    }
}

