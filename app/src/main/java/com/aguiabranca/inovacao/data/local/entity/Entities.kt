package com.aguiabranca.inovacao.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val name: String,
    val role: String,
    val profilePictureUrl: String?,
    val createdAt: Long,
    val lastLogin: Long,
    val isActive: Boolean,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "strategies")
data class StrategyEntity(
    @PrimaryKey val remoteId: String,
    val title: String,
    val description: String,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean,
    val priority: Int,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ideas")
data class IdeaEntity(
    @PrimaryKey val remoteId: String,
    val title: String,
    val description: String,
    val type: String,
    val status: String,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val priority: Int,
    val estimatedImpact: String,
    val department: String,
    val approvedBy: String?,
    val rejectionReason: String?,
    val views: Int,
    val aiScore: Int?,
    val aiFeedback: String?,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val remoteId: String,
    val title: String,
    val description: String,
    val stage: String,
    val status: String,
    val createdBy: String,
    val owner: String,
    val createdAt: Long,
    val updatedAt: Long,
    val startDate: Long,
    val endDate: Long,
    val investment: Double,
    val roi: Double,
    val profit: Double,
    val costReduction: Double,
    val productivityGain: Double,
    val progress: Int,
    val relatedIdeas: List<String>,
    val team: List<String>,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dashboard_snapshots")
data class DashboardSnapshotEntity(
    @PrimaryKey val id: String = "leadership",
    val projectCount: Int,
    val completedProjects: Int,
    val totalInvestment: Double,
    val totalProfit: Double,
    val averageRoi: Double,
    val totalCostReduction: Double,
    val averageProductivityGain: Double,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pending_actions")
data class PendingActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionType: String,
    val pendingPayload: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastError: String? = null
)

