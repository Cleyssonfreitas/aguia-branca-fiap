package com.aguiabranca.inovacao.domain.repository

import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateIdeaRequest
import com.aguiabranca.inovacao.domain.ReviewIdeaRequest
import com.aguiabranca.inovacao.models.Idea

interface IdeaRepository {
    suspend fun createIdea(request: CreateIdeaRequest): AppResult<Idea>
    suspend fun listMyIdeas(): AppResult<List<Idea>>
    suspend fun listIdeasForReview(): AppResult<List<Idea>>
    suspend fun reviewIdea(request: ReviewIdeaRequest): AppResult<Idea>
}

