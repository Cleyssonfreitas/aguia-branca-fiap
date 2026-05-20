package com.aguiabranca.inovacao.network

import com.aguiabranca.inovacao.models.Idea
import com.aguiabranca.inovacao.models.Project
import com.aguiabranca.inovacao.models.Strategy
import retrofit2.http.*


interface ApiService {

    companion object {
        const val BASE_URL = "https://api.sua-url.com/" // Será substituída por MockAPI
    }

    // ==================== STRATEGIES ====================

    @GET("strategies")
    suspend fun getAllStrategies(): List<Strategy>

    @GET("strategies/{id}")
    suspend fun getStrategyById(@Path("id") id: String): Strategy

    @POST("strategies")
    suspend fun createStrategy(@Body strategy: Strategy): Strategy

    @PUT("strategies/{id}")
    suspend fun updateStrategy(
        @Path("id") id: String,
        @Body strategy: Strategy
    ): Strategy

    @DELETE("strategies/{id}")
    suspend fun deleteStrategy(@Path("id") id: String)

    // ==================== IDEAS ====================

    @GET("ideas")
    suspend fun getAllIdeas(): List<Idea>

    @GET("ideas/{id}")
    suspend fun getIdeaById(@Path("id") id: String): Idea

    @GET("ideas/user/{userId}")
    suspend fun getIdeasByUser(@Path("userId") userId: String): List<Idea>

    @POST("ideas")
    suspend fun createIdea(@Body idea: Idea): Idea

    @PUT("ideas/{id}")
    suspend fun updateIdea(
        @Path("id") id: String,
        @Body idea: Idea
    ): Idea

    @PATCH("ideas/{id}/approve")
    suspend fun approveIdea(
        @Path("id") id: String,
        @Query("approvedBy") approvedBy: String
    )

    @PATCH("ideas/{id}/reject")
    suspend fun rejectIdea(
        @Path("id") id: String,
        @Query("reason") reason: String
    )

    @DELETE("ideas/{id}")
    suspend fun deleteIdea(@Path("id") id: String)

    // ==================== PROJECTS ====================

    @GET("projects")
    suspend fun getAllProjects(): List<Project>

    @GET("projects/{id}")
    suspend fun getProjectById(@Path("id") id: String): Project

    @GET("projects/owner/{ownerId}")
    suspend fun getProjectsByOwner(@Path("ownerId") ownerId: String): List<Project>

    @POST("projects")
    suspend fun createProject(@Body project: Project): Project

    @PUT("projects/{id}")
    suspend fun updateProject(
        @Path("id") id: String,
        @Body project: Project
    ): Project

    @PATCH("projects/{id}/progress")
    suspend fun updateProjectProgress(
        @Path("id") id: String,
        @Query("progress") progress: Int
    )

    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: String)
}

