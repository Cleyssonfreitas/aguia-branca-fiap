package com.aguiabranca.inovacao.data.repository

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

