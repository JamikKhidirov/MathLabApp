package com.example.domain.data.navigationRouting

import com.example.domain.data.stateMathlabCategory.MathCategory
import kotlinx.serialization.Serializable


@Serializable
data class NavRouteDecisionScreen(
    val category: MathCategory = MathCategory.ALGEBRA
)
