package com.example.domain.data

data class CategoryItem(
    val icon: Int,
    val title: String,
    val declaration: String,
    val list: List<TaskItem>
)
