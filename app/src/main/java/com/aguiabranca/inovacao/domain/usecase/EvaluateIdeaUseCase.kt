package com.aguiabranca.inovacao.domain.usecase

import com.aguiabranca.inovacao.network.ApiService
import com.aguiabranca.inovacao.network.Content
import com.aguiabranca.inovacao.network.GeminiRequest
import com.aguiabranca.inovacao.network.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AiEvaluation(
    val score: Int,
    val feedback: String
)

class EvaluateIdeaUseCase(private val apiService: ApiService) {

    // A chave real gerada pelo usuário
    private val apiKey = "AIzaSyDdjMkNI5RByHf4NQrIRWSz76-4iP_6cnk"

    suspend fun evaluate(title: String, description: String, estimatedImpact: String): AiEvaluation? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Aja como um comitê de inovação de uma empresa de logística (Águia Branca).
                Avalie a seguinte ideia sugerida por um funcionário.
                
                Título: $title
                Descrição: $description
                Impacto Estimado: $estimatedImpact
                
                Sua resposta DEVE ser estritamente no seguinte formato:
                NOTA: [Um número de 0 a 100]
                FEEDBACK: [Um parágrafo curto de no máximo 3 linhas explicando o porquê da nota, destacando viabilidade e impacto]
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                )
            )

            val response = apiService.generateContent(apiKey, request)
            
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return@withContext null
            
            // Parse do formato exigido
            val scoreRegex = Regex("NOTA:\\s*(\\d+)")
            val feedbackRegex = Regex("FEEDBACK:\\s*(.*)", RegexOption.DOT_MATCHES_ALL)
            
            val score = scoreRegex.find(responseText)?.groupValues?.get(1)?.toIntOrNull() ?: 50
            val feedback = feedbackRegex.find(responseText)?.groupValues?.get(1)?.trim() ?: "Análise não retornou feedback estruturado."
            
            AiEvaluation(score = score, feedback = feedback)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
