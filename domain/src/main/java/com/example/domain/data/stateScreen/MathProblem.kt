package com.example.domain.data.stateScreen

import com.example.domain.data.stateMathlabCategory.MathCategory

data class MathProblem(
    val expression: String,
    val variable: String = "x",
    val category: MathCategory
)
