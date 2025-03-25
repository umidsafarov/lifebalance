package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.uicomponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.defaults.DefaultPreloader
import com.gmail.umidsafarov.lifebalance.presentation.ui.utils.extensions.OnBottomReached


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasksList(
    tasks: List<TaskState>,
    isLoadingNext: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: ((task: TaskState) -> Unit)? = null,
    onItemLongClick: ((task: TaskState) -> Unit)? = null,
    onBottomReached: (() -> Unit)? = null,
) {
    val listState = rememberLazyGridState()
    listState.OnBottomReached(onLoadMore = {
        onBottomReached?.invoke()
    })

    LazyVerticalGrid(
        state = listState,
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = modifier
    ) {
        itemsIndexed(items = tasks, key = { _, item -> item.data.id }) { _, task ->
            TaskItem(
                title = task.data.title.orEmpty(),
                colorIndex = task.data.colorIndex,
                isSelected = task.isSelected.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(7.dp)
                    .combinedClickable(
                        onClick = { onItemClick?.invoke(task) },
                        onLongClick = { onItemLongClick?.invoke(task) }
                    ),
            )
        }
        if (isLoadingNext) {
            item {
                DefaultPreloader(
                    size = 15.dp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .testTag(
                            stringResource(R.string.compose_test_tag_item_preloader)
                        ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeBalanceTheme {
        TasksList(
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
                        colorIndex = 1,
                    ),
                    isSelected = remember { mutableStateOf(true) },
                ),
            ),
            isLoadingNext = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}