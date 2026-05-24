package com.aguiabranca.inovacao.data.local

import com.aguiabranca.inovacao.data.local.entity.DashboardSnapshotEntity
import com.aguiabranca.inovacao.data.local.entity.IdeaEntity
import com.aguiabranca.inovacao.data.local.entity.ProjectEntity
import com.aguiabranca.inovacao.data.local.entity.StrategyEntity
import com.aguiabranca.inovacao.data.local.entity.UserProfileEntity
import com.aguiabranca.inovacao.domain.DashboardSummary
import com.aguiabranca.inovacao.models.Idea
import com.aguiabranca.inovacao.models.Project
import com.aguiabranca.inovacao.models.Strategy
import com.aguiabranca.inovacao.models.User

fun User.toEntity() = UserProfileEntity(uid, email, name, role, createdAt, lastLogin, isActive)
fun UserProfileEntity.toModel() = User(uid, email, name, role, createdAt, lastLogin, isActive)

fun Strategy.toEntity() = StrategyEntity(id, title, description, createdBy, createdAt, updatedAt, isActive, priority)
fun StrategyEntity.toModel() = Strategy(remoteId, title, description, createdBy, createdAt, updatedAt, isActive, priority)

fun Idea.toEntity() = IdeaEntity(
    id,
    title,
    description,
    type,
    status,
    createdBy,
    createdAt,
    updatedAt,
    priority,
    estimatedImpact,
    department,
    approvedBy,
    rejectionReason,
    views
)

fun IdeaEntity.toModel() = Idea(
    remoteId,
    title,
    description,
    type,
    status,
    createdBy,
    createdAt,
    updatedAt,
    priority,
    estimatedImpact,
    department,
    approvedBy,
    rejectionReason,
    views
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

