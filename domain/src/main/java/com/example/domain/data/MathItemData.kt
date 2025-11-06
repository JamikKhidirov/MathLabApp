package com.example.domain.data

import com.example.domain.data.stateMathlabCategory.MathCategory



data class MathItemData(
    val icon: Int?,
    val title: String,
    val description: String,
    val category: MathCategory
)