package com.gmail.umidsafarov.lifebalance.data.local.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "colorIndex") val colorIndex: Int?,
)