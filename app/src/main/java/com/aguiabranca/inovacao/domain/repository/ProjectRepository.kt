package com.aguiabranca.inovacao.domain.repository

import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveProjectRequest
import com.aguiabranca.inovacao.models.Project

interface ProjectRepository {
    suspend fun listProjects(): AppResult<List<Project>>
    suspend fun saveProject(request: SaveProjectRequest): AppResult<Project>
}

