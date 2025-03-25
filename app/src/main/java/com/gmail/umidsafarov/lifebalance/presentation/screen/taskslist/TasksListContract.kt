package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist

import com.gmail.umidsafarov.lifebalance.common.model.OperationError
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState

class TasksListContract {
    data class State(
        val tasks: List<TaskState>,
        val isLoading: Boolean = false,
    )

    sealed class Event {
        data class NavigateToTask(val id: Long?) : Event()
        data class Error(val error: OperationError) : Event()
    }

    sealed class UIEvent {
        data object CreateTask : UIEvent()
        data class TaskChosen(val task: TaskState) : UIEvent()
        data class TaskSelected(val task: TaskState) : UIEvent()
        data object LoadNext : UIEvent()
        data class LoadTask(val id: Long) : UIEvent()
    }
}