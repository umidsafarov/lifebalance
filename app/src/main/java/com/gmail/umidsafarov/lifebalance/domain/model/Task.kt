package com.gmail.umidsafarov.lifebalance.domain.model

data class Task(
    val id: Long,
    val title: String?,
    val description: String?,
    val colorIndex: Int?,
)