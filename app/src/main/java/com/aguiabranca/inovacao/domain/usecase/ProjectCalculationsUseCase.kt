package com.aguiabranca.inovacao.domain.usecase

object ProjectCalculationsUseCase {
    
    fun calculateROI(investment: Double, return_: Double): Double {
        if (investment <= 0) return 0.0
        return ((return_ - investment) / investment) * 100
    }

    fun calculateTotalROI(totalReturn: Double, totalInvestment: Double): Double {
        if (totalInvestment <= 0) return 0.0
        return ((totalReturn - totalInvestment) / totalInvestment) * 100
    }

    fun calculateProfit(return_: Double, investment: Double): Double {
        return return_ - investment
    }

    fun calculateProductivityGain(baselineMetric: Double, currentMetric: Double): Double {
        if (baselineMetric <= 0) return 0.0
        return ((currentMetric - baselineMetric) / baselineMetric) * 100
    }

    fun calculateCostReduction(baseCost: Double, newCost: Double): Double {
        if (baseCost <= 0) return 0.0
        return ((baseCost - newCost) / baseCost) * 100
    }

    fun estimatePaybackPeriod(investment: Double, monthlyReturn: Double): Double {
        if (monthlyReturn <= 0) return 0.0
        return investment / monthlyReturn
    }
}

