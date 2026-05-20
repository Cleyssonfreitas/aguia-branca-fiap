package com.aguiabranca.inovacao.utils

object Constants {
    
    const val FIREBASE_URL = "https://seu-projeto.firebaseio.com"
    const val USERS_TABLE = "users"
    const val IDEAS_TABLE = "ideas"
    const val STRATEGIES_TABLE = "strategies"
    const val PROJECTS_TABLE = "projects"

    const val MOCK_API_URL = "https://sua-mockapi.com/api/"
    const val API_TIMEOUT = 30L

    object Logging {
        const val TAG_AUTH = "AuthRepository"
        const val TAG_IDEA = "IdeaRepository"
        const val TAG_API = "ApiService"
    }

    object ErrorMessages {
        const val INVALID_CREDENTIALS = "Email ou senha inválidos"
        const val USER_NOT_FOUND = "Usuário não encontrado"
        const val NETWORK_ERROR = "Erro de conexão com a rede"
        const val UNKNOWN_ERROR = "Erro desconhecido"
    }
}

