package com.gmail.umidsafarov.lifebalance.data

import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import com.gmail.umidsafarov.lifebalance.domain.model.Task

fun TaskEntity.toTask(): Task? {
    if (id == null) return null
    return Task(id = id, title = title, description = description, colorIndex = colorIndex)
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(id = id, title = title, description = description, colorIndex = colorIndex)
}