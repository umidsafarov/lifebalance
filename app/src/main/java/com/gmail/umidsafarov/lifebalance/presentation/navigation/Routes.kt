package com.gmail.umidsafarov.lifebalance.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object TasksListRoute : Routes() {
        const val RESULT_TASK_ID = "result_task_id"
    }

    @Serializable
    data class TaskRoute(val id: Long?) : Routes()
}