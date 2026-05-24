package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.Strategy
import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.DashboardSummary

internal fun Map<*, *>.toUser(): User {
    return User(
        uid = readString("uid"),
        email = readString("email"),
        name = readString("name"),
        role = readString("role", "OPERADOR"),
        createdAt = readLong("createdAt"),
        lastLogin = readLong("lastLogin"),
        isActive = readBoolean("isActive", true)
    )
}

internal fun Map<*, *>.toStrategy(): Strategy {
    return Strategy(
        id = readString("id"),
        title = readString("title"),
        description = readString("description"),
        createdBy = readString("createdBy"),
        createdAt = readLong("createdAt"),
        updatedAt = readLong("updatedAt"),
        isActive = readBoolean("isActive", true),
        priority = readInt("priority", 1)
    )
}

internal fun Map<*, *>.toIdea(): Idea {
    return Idea(
        id = readString("id"),
        title = readString("title"),
        description = readString("description"),
        type = readString("type", "PROBLEMA"),
        status = readString("status", "SUBMETIDA"),
        createdBy = readString("createdBy"),
        createdAt = readLong("createdAt"),
        updatedAt = readLong("updatedAt"),
        priority = readInt("priority"),
        estimatedImpact = readString("estimatedImpact"),
        department = readString("department"),
        approvedBy = readNullableString("approvedBy"),
        rejectionReason = readNullableString("rejectionReason"),
        views = readInt("views")
    )
}

internal fun Map<*, *>.toProject(): Project {
    return Project(
        id = readString("id"),
        title = readString("title"),
        description = readString("description"),
        stage = readString("stage", "Planejamento"),
        status = readString("status", "PLANEJAMENTO"),
        createdBy = readString("createdBy"),
        owner = readString("owner"),
        createdAt = readLong("createdAt"),
        updatedAt = readLong("updatedAt"),
        startDate = readLong("startDate"),
        endDate = readLong("endDate"),
        investment = readDouble("investment"),
        roi = readDouble("roi"),
        profit = readDouble("profit"),
        costReduction = readDouble("costReduction"),
        productivityGain = readDouble("productivityGain"),
        progress = readInt("progress"),
        relatedIdeas = readStringList("relatedIdeas"),
        team = readStringList("team")
    )
}

internal fun Map<*, *>.toDashboardSummary(): DashboardSummary {
    return DashboardSummary(
        projectCount = readInt("projectCount"),
        completedProjects = readInt("completedProjects"),
        totalInvestment = readDouble("totalInvestment"),
        totalProfit = readDouble("totalProfit"),
        averageRoi = readDouble("averageRoi"),
        totalCostReduction = readDouble("totalCostReduction"),
        averageProductivityGain = readDouble("averageProductivityGain")
    )
}

private fun Map<*, *>.readString(key: String, default: String = "") = this[key]?.toString() ?: default
private fun Map<*, *>.readNullableString(key: String) = this[key]?.toString()
private fun Map<*, *>.readBoolean(key: String, default: Boolean = false) = this[key] as? Boolean ?: default
private fun Map<*, *>.readLong(key: String) = (this[key] as? Number)?.toLong() ?: 0L
private fun Map<*, *>.readInt(key: String, default: Int = 0) = (this[key] as? Number)?.toInt() ?: default
private fun Map<*, *>.readDouble(key: String) = (this[key] as? Number)?.toDouble() ?: 0.0

private fun Map<*, *>.readStringList(key: String): List<String> {
    val value = this[key] as? List<*> ?: return emptyList()
    return value.mapNotNull { it?.toString() }
}
