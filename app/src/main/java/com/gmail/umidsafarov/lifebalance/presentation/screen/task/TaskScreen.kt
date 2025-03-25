package com.gmail.umidsafarov.lifebalance.presentation.screen.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
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
import com.gmail.umidsafarov.lifebalance.presentation.screen.task.uicomponents.TaskDetails
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LightRed
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.defaults.DefaultPreloader
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.popup.ColorSelectionDialog
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.popup.SnackbarMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TaskScreen(
    state: TaskContract.State,
    events: Flow<TaskContract.Event>,
    sendEvent: (event: TaskContract.UIEvent) -> Unit,
    onNavigateBack: (resultId: Long?) -> Unit,
) {
    //context
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //states
    val snackbarHostState = remember { SnackbarHostState() }
    val isColorSelectionActive = remember { mutableStateOf(false) }

    //single time events
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect { event ->
                    when (event) {
                        is TaskContract.Event.NavigateBack -> {
                            onNavigateBack(event.resultId)
                        }

                        is TaskContract.Event.Error -> {
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

    Crossfade(
        targetState = state.isLoading,
        label = "",
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
    ) { isLoading ->
        if (isLoading) {
            DefaultPreloader(
                size = 100.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(
                        stringResource(R.string.compose_test_tag_preloader)
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                TaskDetails(
                    state = state,
                    sendEvent = sendEvent,
                    onChooseColor = {
                        isColorSelectionActive.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 50.dp, vertical = 100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmallFloatingActionButton(
                        onClick = { sendEvent(TaskContract.UIEvent.Remove) },
                        shape = CircleShape,
                        containerColor = LightRed,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.content_description_delete_task),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(
                        onClick = { sendEvent(TaskContract.UIEvent.Save) },
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = stringResource(R.string.content_description_save_task),
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(isColorSelectionActive.value) {
        ColorSelectionDialog(
            currentColorIndex = state.colorIndex,
            onColorSelected = {
                sendEvent(TaskContract.UIEvent.ColorChanged(it))
            },
            onDismiss = { isColorSelectionActive.value = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewData() {
    LifeBalanceTheme {
        TaskScreen(
            state = TaskContract.State(
                title = "Sample task title",
                description = "Some description",
                colorIndex = 0,
                isLoading = false,
            ),
            events = flow { },
            sendEvent = { },
            onNavigateBack = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoading() {
    LifeBalanceTheme {
        TaskScreen(
            state = TaskContract.State(
                title = null,
                description = null,
                colorIndex = null,
                isLoading = true,
            ),
            events = flow { },
            sendEvent = { },
            onNavigateBack = { },
        )
    }
}