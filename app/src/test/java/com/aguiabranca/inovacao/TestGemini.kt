package com.aguiabranca.inovacao

import kotlinx.coroutines.runBlocking
import org.junit.Test
import com.aguiabranca.inovacao.network.RetrofitClient
import com.aguiabranca.inovacao.domain.usecase.EvaluateIdeaUseCase

class TestGemini {
    @Test
    fun testGeminiApi() = runBlocking {
        val useCase = EvaluateIdeaUseCase(RetrofitClient.getApiService())
        val result = useCase.evaluate("Carona Solidária", "Criar um app para os funcionários", "Economia de gasolina")
        println("Result from Gemini API: $result")
        assert(result != null)
    }
}
