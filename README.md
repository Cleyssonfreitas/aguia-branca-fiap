# Águia Branca Inovação

App mobile para gestão de inovação corporativa desenvolvido para o Challenge do Grupo Águia Branca (Sprint 1).

---

## Firebase local config

O arquivo real `app/google-services.json` nao deve ser versionado. Para configurar o projeto localmente, copie `app/google-services.example.json` para `app/google-services.json` e preencha os valores gerados no Firebase Console.

---

# Data Layer - Arquitetura de Dados e Comunicação

Este módulo contém a implementação da camada de dados do aplicativo, incluindo autenticação com Firebase Auth, persistência em tempo real com Firebase Realtime Database e comunicação HTTP com APIs externas via Retrofit.

## 📋 Estrutura de Pastas

```
app/src/main/java/com/aguiabranca/inovacao/
├── data/
│   └── repository/
│       ├── AuthRepository.kt      # Autenticação Firebase
│       └── IdeaRepository.kt      # CRUD de Ideias
├── models/
│   ├── User.kt                    # Usuário e níveis de acesso (UserRole)
│   ├── Idea.kt                    # Ideias de inovação
│   ├── Strategy.kt                # Orientações Estratégicas
│   └── Project.kt                 # Projetos corporativos
├── network/
│   ├── ApiService.kt              # Interface de endpoints REST (Retrofit)
│   └── RetrofitClient.kt          # Inicialização do cliente HTTP com OkHttp e Gson
└── utils/
    └── Constants.kt               # Endpoints e mensagens do sistema
```

## 🔐 AuthRepository - Autenticação Firebase

### Funcionalidades:
*   `signIn()`: Login seguro com email/senha e validação de privilégios.
*   `signUp()`: Cadastro de novos usuários salvando o perfil no Realtime Database (`users/`).
*   `signOut()`: Encerramento seguro da sessão.
*   `getCurrentUser()`: Recuperação do usuário autenticado no momento.
*   `updateUserData()`: Atualização de perfil do usuário.
*   `isUserLoggedIn()`: Verificação de sessão ativa.

### Exemplo de uso:
```kotlin
val authRepo = AuthRepository()

// Realizar Login
when (val result = authRepo.signIn("user@email.com", "<senha>")) {
    is AuthResult.Success -> {
        val user = result.data
        println("Logado como: ${user.name} | Cargo: ${user.role}")
    }
    is AuthResult.Error -> {
        println("Erro no login: ${result.exception.message}")
    }
    AuthResult.Loading -> { /* Exibir loading */ }
}
```

---

## 💡 IdeaRepository - CRUD de Ideias (Firebase)

### Funcionalidades:
*   `createIdea()`: Envia uma nova ideia/problema operacional gerando um ID exclusivo.
*   `getIdeaById()`: Busca detalhes de uma proposta.
*   `getIdeasByUser()`: Filtra ideias cadastradas por um operador específico.
*   `getAllIdeasLiveData()`: Retorna um fluxo observável (`LiveData`) em tempo real contendo todas as ideias.
*   `updateIdea()`: Atualiza dados de ideias em edição/rascunho.
*   `approveIdea()` / `rejectIdea()`: Métodos para aprovação e justificativa de rejeição.
*   `deleteIdea()`: Exclusão de registro.

### Exemplo de uso:
```kotlin
val ideaRepo = IdeaRepository()

// Criar nova ideia (Operador)
val newIdea = Idea(
    title = "Otimização de rotas de entrega",
    description = "Sugestão de reordenamento no hub central...",
    type = IdeaType.MELHORIA.name,
    createdBy = "userUid123"
)

when (val result = ideaRepo.createIdea(newIdea)) {
    is AuthResult.Success -> println("Ideia cadastrada com ID: ${result.data}")
    is AuthResult.Error -> println("Falha ao salvar: ${result.exception.message}")
    AuthResult.Loading -> { }
}

// Escuta em tempo real (UI)
ideaRepo.getAllIdeasLiveData().observe(viewLifecycleOwner) { listaDeIdeias ->
    // Atualiza a listagem no Compose
}
```

---

## 🌐 Retrofit - APIs REST

### Endpoints declarados (`ApiService.kt`):
*   `GET /strategies` / `POST /strategies`: Manipulação das orientações estratégicas.
*   `GET /ideas` / `POST /ideas` / `PATCH /ideas/{id}/approve`: Gestão alternativa de ideias.
*   `GET /projects` / `POST /projects` / `PATCH /projects/{id}/progress`: Gestão do ciclo de vida dos projetos.

### Exemplo de uso:
```kotlin
val apiService = RetrofitClient.getApiService()

// Consumo assíncrono em Coroutine
try {
    val projetos = apiService.getAllProjects()
    // Atualizar UI com os projetos e progresso
} catch (e: Exception) {
    // Tratar erro de conexão
}
```

---

## 🏗️ Padrão de Resultado: `AuthResult<T>`

Todas as chamadas assíncronas dos repositórios são encapsuladas na `sealed class` `AuthResult`, permitindo um controle de estado robusto na interface gráfica:

```kotlin
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}
```

---

## ⚙️ Próximas etapas:
*   [ ] Adicionar plugin do Google Services no Gradle.
*   [ ] Inserir arquivo `google-services.json` na pasta `/app`.
*   [ ] Implementar `ProjectRepository` e `StrategyRepository` para o fluxo de projetos e diretrizes estratégicas.
*   [ ] Criar os ViewModels para fazer a ponte entre as telas e repositórios.
*   [ ] Desenvolver as telas com Compose utilizando Material Design 3.
