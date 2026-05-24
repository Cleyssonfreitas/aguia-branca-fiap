package com.aguiabranca.inovacao.utils

import com.aguiabranca.inovacao.domain.models.UserRole

fun String.toRole(): UserRole = UserRole.entries.firstOrNull { it.name == this } ?: UserRole.OPERADOR

fun UserRole.shortName(): String = when (this) {
    UserRole.ADMIN_TI -> "TI"
    UserRole.LIDERANCA -> "Líder"
    UserRole.GESTOR -> "Gestor"
    UserRole.OPERADOR -> "Operador"
}

fun Double.formatMoney(): String = String.format("%.2f", this)

fun Double.formatPercent(): String = String.format("%.1f%%", this)
