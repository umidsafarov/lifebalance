package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gmail.umidsafarov.lifebalance.domain.model.Task

data class TaskState(
    val data: Task,
    val isSelected: MutableState<Boolean> = mutableStateOf(false),
)
