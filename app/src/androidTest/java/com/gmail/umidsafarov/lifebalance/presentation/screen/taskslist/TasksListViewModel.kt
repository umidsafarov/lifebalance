package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist

import androidx.compose.runtime.mutableStateOf
import androidx.test.filters.SmallTest
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.usecase.AddTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTasksListUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.UpdateTaskUseCase
import com.gmail.umidsafarov.lifebalance.extension.collectInBackground
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState
import com.gmail.umidsafarov.lifebalance.presentation.Config
import com.gmail.umidsafarov.lifebalance.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@SmallTest
@HiltAndroidTest
class TasksListViewModelTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var getTasksListUseCase: GetTasksListUseCase

    @Inject
    lateinit var getTaskUseCase: GetTaskUseCase

    @Inject
    lateinit var addTaskUseCase: AddTaskUseCase

    @Inject
    lateinit var updateTaskUseCase: UpdateTaskUseCase

    private lateinit var viewModel: TasksListViewModel

    @Before
    fun setUp() = runTest {
        hiltRule.inject()

        viewModel = TasksListViewModel(getTasksListUseCase, getTaskUseCase)
    }

    @Test
    fun init_shouldHaveEmptyTaskListAndSetLoadingStateToFalse() = runTest {
        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks.size).isEqualTo(0)
    }

    @Test
    fun init_shouldLoadInitialTasksAndSetLoadingStateToFalse() = runTest {
        // Arrange

        addTaskUseCase("Task 1", null, null)
        addTaskUseCase("Task 2", null, null)
        viewModel = TasksListViewModel(
            getTasksListUseCase, getTaskUseCase,
        )

        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks.size).isEqualTo(2)
        assertThat(viewModel.state.value.tasks[0].data.title).isEqualTo("Task 2")
        assertThat(viewModel.state.value.tasks[1].data.title).isEqualTo("Task 1")
    }

    @Test
    fun loadNextTasksPage_shouldLoadNextPageOfTasksAndAppendToExistingList() =
        runTest {
            // Arrange
            for (i in 0 until 3) {
                addTaskUseCase("Task $i", null, null)
            }

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

            // Assert
            assertThat(viewModel.state.value.isLoading).isFalse()
            assertThat(viewModel.state.value.tasks.size).isEqualTo(3)
            assertThat(viewModel.state.value.tasks[0].data.title).isEqualTo("Task 2")
            assertThat(viewModel.state.value.tasks[1].data.title).isEqualTo("Task 1")
            assertThat(viewModel.state.value.tasks[2].data.title).isEqualTo("Task 0")
        }

    @Test
    fun handleUIEvent_CreateTask_shouldEmitNavigateToTaskEventWithNullId() = runTest {
        // Arrange
        val values = collectInBackground(viewModel.events)

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.CreateTask)

        // Assert
        assertThat(values).isNotEmpty()
        assertThat(values[0]).isInstanceOf(TasksListContract.Event.NavigateToTask::class.java)
        assertThat((values[0] as TasksListContract.Event.NavigateToTask).id).isNull()
    }

    @Test
    fun handleUIEvent_TaskChosen_shouldEmitNavigateToTaskEventWithTaskId() = runTest {
        // Arrange
        val task = TaskState(
            Task(id = 5, title = "Task 5", description = "Description 5", colorIndex = null),
            mutableStateOf(false)
        )
        val values = collectInBackground(viewModel.events)

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.TaskChosen(task))

        // Assert
        assertThat(values).isNotEmpty()
        assertThat(values[0]).isInstanceOf(TasksListContract.Event.NavigateToTask::class.java)
        assertThat((values[0] as TasksListContract.Event.NavigateToTask).id).isEqualTo(5L)
    }

    @Test
    fun handleUIEvent_TaskSelected_shouldToggleIsSelectedState() = runTest {
        // Arrange
        val task = TaskState(
            Task(id = 5, title = "Task 5", description = "Description 5", colorIndex = null),
            mutableStateOf(false)
        )

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.TaskSelected(task))

        // Assert
        assertThat(task.isSelected.value).isTrue()

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.TaskSelected(task))

        // Assert
        assertThat(task.isSelected.value).isFalse()
    }

    @Test
    fun handleUIEvent_LoadNext_shouldLoadNextTasksPage() = runTest {
        // Arrange
        for (i in 0 until Config.ITEMS_PER_PAGE * 2) {
            addTaskUseCase("Task $i", null, null)
        }

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks).hasSize(Config.ITEMS_PER_PAGE * 2)
        assertThat(viewModel.state.value.tasks[Config.ITEMS_PER_PAGE].data.title)
            .isEqualTo("Task ${Config.ITEMS_PER_PAGE - 1}")
    }

    @Test
    fun handleUIEvent_LoadNextBeyondLastPage_doesNotChangeTasksList() = runTest {
        // Arrange
        for (i in 0 until 3) {
            addTaskUseCase("Task $i", null, null)
        }

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks).hasSize(3)
    }

    @Test
    fun handleUIEvent_RefreshTask_whenTaskIsNotLoadedShouldAddItToTheStartOfTheList() = runTest {
        // Arrange
        for (i in 0 until 4) {
            addTaskUseCase("Task $i", null, null)
        }
        val task = Task(id = 5, title = "Task 5", description = "Description 5", colorIndex = null)
        addTaskUseCase(task.title, task.description, task.colorIndex)

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(task.id))

        // Assert
        val tasks = viewModel.state.value.tasks
        assertThat(tasks).hasSize(5)
        assertThat(tasks[0].data).isEqualTo(task)
        assertThat(tasks[4].data).isNotEqualTo(task)
    }

    @Test
    fun handleUIEvent_RefreshTask_whenTaskIsLoadedShouldRefreshItsData() = runTest {
        // Arrange
        addTaskUseCase("Task 1", null, null)

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

        // Assert
        val tasks = viewModel.state.value.tasks
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].data.title).isEqualTo("Task 1")

        // Arrange
        updateTaskUseCase(tasks[0].data.copy(title = "Task 1 updated"))

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(tasks[0].data.id))

        // Assert
        val updatedTasks = viewModel.state.value.tasks
        assertThat(updatedTasks).hasSize(1)
        assertThat(updatedTasks[0].data.title).isEqualTo("Task 1 updated")
    }

}