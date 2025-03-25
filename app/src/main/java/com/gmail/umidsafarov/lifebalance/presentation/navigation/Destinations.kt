package com.gmail.umidsafarov.lifebalance.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.gmail.umidsafarov.lifebalance.presentation.screen.task.TaskScreen
import com.gmail.umidsafarov.lifebalance.presentation.screen.task.TaskViewModel
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.TasksListContract
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.TasksListScreen
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.TasksListViewModel

@Composable
fun TasksListDestination(
    route: Routes.TasksListRoute,
    onNavigateToTask: (id: Long?) -> Unit,
    resultTaskId: Long?,
    onResultTaskIdHandled: () -> Unit = {},
) {
    val viewModel: TasksListViewModel = hiltViewModel()
    LaunchedEffect(resultTaskId) {
        resultTaskId?.let {
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(it))
            onResultTaskIdHandled()
        }
    }
    TasksListScreen(
        state = viewModel.state.value,
        events = viewModel.events,
        sendEvent = viewModel::handleUIEvent,
        onNavigateToTask = onNavigateToTask,
    )
}


@Composable
fun TaskDestination(route: Routes.TaskRoute, onBack: (resultId: Long?) -> Unit) {
    val viewModel =
        hiltViewModel<TaskViewModel, TaskViewModel.Factory>() { factory -> factory.create(route.id) }
    TaskScreen(
        state = viewModel.state.value,
        events = viewModel.events,
        sendEvent = viewModel::handleUIEvent,
        onNavigateBack = onBack,
    )
}