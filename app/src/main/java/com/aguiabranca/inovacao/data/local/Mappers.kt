package com.aguiabranca.inovacao.data.local

import com.aguiabranca.inovacao.data.local.entity.DashboardSnapshotEntity
import com.aguiabranca.inovacao.data.local.entity.IdeaEntity
import com.aguiabranca.inovacao.data.local.entity.ProjectEntity
import com.aguiabranca.inovacao.data.local.entity.StrategyEntity
import com.aguiabranca.inovacao.data.local.entity.UserProfileEntity
import com.aguiabranca.inovacao.domain.DashboardSummary
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.Strategy
import com.aguiabranca.inovacao.domain.models.User

fun User.toEntity() = UserProfileEntity(uid, email, name, role, profilePictureUrl, createdAt, lastLogin, isActive)
fun UserProfileEntity.toModel() = User(uid, email, name, role, profilePictureUrl, createdAt, lastLogin, isActive)

fun Strategy.toEntity() = StrategyEntity(id, title, description, createdBy, createdAt, updatedAt, isActive, priority)
fun StrategyEntity.toModel() = Strategy(remoteId, title, description, createdBy, createdAt, updatedAt, isActive, priority)

fun Idea.toEntity() = IdeaEntity(
    remoteId = id,
    title = title,
    description = description,
    type = type,
    status = status,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    priority = priority,
    estimatedImpact = estimatedImpact,
    department = department,
    approvedBy = approvedBy,
    rejectionReason = rejectionReason,
    views = views,
    aiScore = aiScore,
    aiFeedback = aiFeedback
)

fun IdeaEntity.toModel() = Idea(
    id = remoteId,
    title = title,
    description = description,
    type = type,
    status = status,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    priority = priority,
    estimatedImpact = estimatedImpact,
    department = department,
    approvedBy = approvedBy,
    rejectionReason = rejectionReason,
    views = views,
    aiScore = aiScore,
    aiFeedback = aiFeedback
)

fun Project.toEntity() = ProjectEntity(
    id,
    title,
    description,
    stage,
    status,
    createdBy,
    owner,
    createdAt,
    updatedAt,
    startDate,
    endDate,
    investment,
    roi,
    profit,
    costReduction,
    productivityGain,
    progress,
    relatedIdeas,
    team
)

fun ProjectEntity.toModel() = Project(
    remoteId,
    title,
    description,
    stage,
    status,
    createdBy,
    owner,
    createdAt,
    updatedAt,
    startDate,
    endDate,
    investment,
    roi,
    profit,
    costReduction,
    productivityGain,
    progress,
    relatedIdeas,
    team
)

fun DashboardSummary.toEntity() = DashboardSnapshotEntity(
    projectCount = projectCount,
    completedProjects = completedProjects,
    totalInvestment = totalInvestment,
    totalProfit = totalProfit,
    averageRoi = averageRoi,
    totalCostReduction = totalCostReduction,
    averageProductivityGain = averageProductivityGain
)

fun DashboardSnapshotEntity.toModel() = DashboardSummary(
    projectCount,
    completedProjects,
    totalInvestment,
    totalProfit,
    averageRoi,
    totalCostReduction,
    averageProductivityGain
)

