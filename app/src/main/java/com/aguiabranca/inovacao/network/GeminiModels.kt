package com.aguiabranca.inovacao.network

// ==========================================
// REQUEST MODELS
// ==========================================

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

// ==========================================
// RESPONSE MODELS
// ==========================================

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ContentResponse?
)

data class ContentResponse(
    val parts: List<PartResponse>?
)

data class PartResponse(
    val text: String?
)
