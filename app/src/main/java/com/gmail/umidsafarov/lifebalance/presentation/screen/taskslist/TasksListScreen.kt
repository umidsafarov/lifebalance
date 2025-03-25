package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.uicomponents.TasksList
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.defaults.DefaultPreloader
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.placeholder.MessagePlaceholder
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.popup.SnackbarMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TasksListScreen(
    state: TasksListContract.State,
    events: Flow<TasksListContract.Event>,
    sendEvent: (event: TasksListContract.UIEvent) -> Unit,
    onNavigateToTask: (taskId: Long?) -> Unit,
) {
    //context
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //states
    val snackbarHostState = remember { SnackbarHostState() }
    val tasksNotEmpty = remember(key1 = state.tasks.size) {
        state.tasks.isNotEmpty()
    }

    //single time events
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect { event ->
                    when (event) {
                        is TasksListContract.Event.NavigateToTask -> {
                            onNavigateToTask(event.id)
                        }

                        is TasksListContract.Event.Error -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    event.error.message ?: context.getString(
                                        R.string.error_unknown
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

//messages
    SnackbarMessage(snackbarHostState)

//interface
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {
        Crossfade(
            targetState = tasksNotEmpty,
            label = "",
            modifier = Modifier
                .fillMaxSize(),
        ) { notEmpty ->
            if (notEmpty) {
                TasksList(
                    tasks = state.tasks,
                    isLoadingNext = state.isLoading,
                    modifier = Modifier
                        .padding(0.dp, 2.dp, 0.dp, 0.dp)
                        .fillMaxSize(),
                    onItemClick = {
                        sendEvent(
                            TasksListContract.UIEvent.TaskChosen(
                                it
                            )
                        )
                    },
                    onItemLongClick = {
                        sendEvent(
                            TasksListContract.UIEvent.TaskSelected(
                                it
                            )
                        )
                    },
                    onBottomReached = { sendEvent(TasksListContract.UIEvent.LoadNext) },
                )
            } else {
                if (state.isLoading) {
                    DefaultPreloader(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(
                                stringResource(R.string.compose_test_tag_preloader)
                            ),
                        size = 100.dp,
                        thickness = 8.dp,
                    )
                } else {
                    MessagePlaceholder(
                        message = stringResource(R.string.message_empty_items),
                        onClick = { sendEvent(TasksListContract.UIEvent.LoadNext) },
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { sendEvent(TasksListContract.UIEvent.CreateTask) },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 50.dp, vertical = 100.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.content_description_add_task),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTasks() {
    LifeBalanceTheme {
        TasksListScreen(
            state = TasksListContract.State(
                tasks = listOf(
                    TaskState(
                        Task(
                            id = 0,
                            title = "Example task",
                            description = "Detailed description of a task",
                            colorIndex = 0,
                        ),
                        isSelected = remember { mutableStateOf(false) },
                    ),
                    TaskState(
                        Task(
                            id = 1,
                            title = "Second task",
                            description = "Another description of a task",
                            colorIndex = null,
                        ),
                        isSelected = remember { mutableStateOf(true) },
                    ),
                ),
                isLoading = true,
            ),
            events = flow { },
            sendEvent = { },
            onNavigateToTask = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmpty() {
    LifeBalanceTheme {
        TasksListScreen(
            state = TasksListContract.State(
                tasks = listOf(),
                isLoading = false,
            ),
            events = flow { },
            sendEvent = { },
            onNavigateToTask = { },
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewLoading() {
    LifeBalanceTheme {
        TasksListScreen(
            state = TasksListContract.State(
                tasks = listOf(),
                isLoading = true,
            ),
            events = flow { },
            sendEvent = { },
            onNavigateToTask = { },
        )
    }
}