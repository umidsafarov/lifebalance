package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist

import androidx.compose.runtime.mutableStateOf
import com.gmail.umidsafarov.lifebalance.common.model.OperationError
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTasksListUseCase
import com.gmail.umidsafarov.lifebalance.extension.collectInBackground
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState
import com.gmail.umidsafarov.lifebalance.presentation.Config
import com.gmail.umidsafarov.lifebalance.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TasksListRouteViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getTasksListUseCase: GetTasksListUseCase
    private lateinit var getTaskUseCase: GetTaskUseCase
    private lateinit var viewModel: TasksListViewModel

    @Before
    fun setUp() = runTest {
        getTasksListUseCase = mock()
        whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
            OperationResult.Success(listOf())
        )
        getTaskUseCase = mock()
        viewModel = TasksListViewModel(
            getTasksListUseCase, getTaskUseCase,
        )
    }

    @Test
    fun `init - should have empty task list and set loading state to false`() = runTest {
        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks.size).isEqualTo(0)
    }

    @Test
    fun `init - should load initial tasks and set loading state to false`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = null),
            Task(id = 2, title = "Task 2", description = "Description 2", colorIndex = null),
        )
        getTasksListUseCase = mock()
        whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
            OperationResult.Success(
                mockTasks
            )
        )
        viewModel = TasksListViewModel(
            getTasksListUseCase, getTaskUseCase,
        )

        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks.size).isEqualTo(2)
        assertThat(viewModel.state.value.tasks[0].data.title).isEqualTo("Task 1")
        assertThat(viewModel.state.value.tasks[1].data.title).isEqualTo("Task 2")
    }

    @Test
    fun `loadNextTasksPage - should load next page of tasks and append to existing list`() =
        runTest {
            // Arrange
            val initialTasks = listOf(
                Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = null)
            )
            val nextTasks = listOf(
                Task(id = 2, title = "Task 2", description = "Description 2", colorIndex = null),
                Task(id = 3, title = "Task 3", description = "Description 3", colorIndex = null)
            )
            whenever(
                getTasksListUseCase(
                    Config.ITEMS_PER_PAGE,
                    0
                )
            ).thenReturn(OperationResult.Success(initialTasks))
            whenever(
                getTasksListUseCase(
                    Config.ITEMS_PER_PAGE,
                    1
                )
            ).thenReturn(OperationResult.Success(nextTasks))

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

            // Assert
            assertThat(viewModel.state.value.isLoading).isFalse()
            assertThat(viewModel.state.value.tasks.size).isEqualTo(3)
            assertThat(viewModel.state.value.tasks[0].data.title).isEqualTo("Task 1")
            assertThat(viewModel.state.value.tasks[1].data.title).isEqualTo("Task 2")
            assertThat(viewModel.state.value.tasks[2].data.title).isEqualTo("Task 3")
        }

    @Test
    fun `loadNextTasksPage - should emit error event when use case fails`() = runTest {
        // Arrange
        val errorMessage = "Failed to load tasks"
        whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
            OperationResult.Fail(
                OperationError.Unspecified(errorMessage)
            )
        )

        val values = collectInBackground(viewModel.events)

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

        // Assert
        assertThat(values).isNotEmpty()
        assertThat(values[0]).isInstanceOf(TasksListContract.Event.Error::class.java)
        assertThat(errorMessage)
            .isEqualTo((values[0] as TasksListContract.Event.Error).error.message)
    }

    @Test
    fun `handleUIEvent - CreateTask - should emit NavigateToTask event with null id`() = runTest {
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
    fun `handleUIEvent - TaskChosen - should emit NavigateToTask event with task id`() = runTest {
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
    fun `handleUIEvent - TaskSelected - should toggle isSelected state`() = runTest {
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
    fun `handleUIEvent - LoadNext - should load next tasks page`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = null),
            Task(id = 2, title = "Task 2", description = "Description 2", colorIndex = null)
        )
        whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
            OperationResult.Success(
                mockTasks
            )
        )

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks).hasSize(2)
        assertThat(viewModel.state.value.tasks[0].data.title).isEqualTo("Task 1")
        assertThat(viewModel.state.value.tasks[1].data.title).isEqualTo("Task 2")
    }

    @Test
    fun `handleUIEvent - LoadNext beyond last page - does not change tasks list`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = null),
            Task(id = 2, title = "Task 2", description = "Description 2", colorIndex = null)
        )
        whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
            OperationResult.Success(
                mockTasks
            )
        )
        whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 2)).thenReturn(
            OperationResult.Success(
                listOf()
            )
        )

        // Act
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)
        viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

        // Assert
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.tasks).hasSize(2)
        assertThat(viewModel.state.value.tasks[0].data.title).isEqualTo("Task 1")
        assertThat(viewModel.state.value.tasks[1].data.title).isEqualTo("Task 2")
    }

    @Test
    fun `handleUIEvent - RefreshTask - when task is not loaded should add it to the start of the list`() =
        runTest {
            // Arrange
            val task =
                Task(id = 5, title = "Task 5", description = "Description 5", colorIndex = null)
            whenever(getTaskUseCase(task.id)).thenReturn(
                OperationResult.Success(task)
            )

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(task.id))

            // Assert
            val tasks = viewModel.state.value.tasks
            assertThat(tasks).hasSize(1)
            assertThat(tasks[0].data).isEqualTo(task)
        }

    @Test
    fun `handleUIEvent - RefreshTask - when task is loaded should refresh its data`() =
        runTest {
            // Arrange
            val initialTask =
                Task(id = 5, title = "Task 5", description = "Description 5", colorIndex = null)
            val updatedTask = Task(
                id = 5,
                title = "Task 5 updated",
                description = "Description 5 updated",
                colorIndex = null
            )

            whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
                OperationResult.Success(
                    listOf(initialTask)
                )
            )
            whenever(getTaskUseCase(updatedTask.id)).thenReturn(
                OperationResult.Success(updatedTask)
            )

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

            // Assert
            val tasks = viewModel.state.value.tasks
            assertThat(tasks).hasSize(1)
            assertThat(tasks[0].data).isEqualTo(initialTask)

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(updatedTask.id))

            // Assert
            val updatedTasks = viewModel.state.value.tasks
            assertThat(updatedTasks).hasSize(1)
            assertThat(updatedTasks[0].data).isEqualTo(updatedTask)
        }

    @Test
    fun `handleUIEvent - RefreshTask - when task does not exist removes task from the list`() =
        runTest {
            // Arrange
            val tasksList = listOf(
                Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = null),
                Task(id = 2, title = "Task 2", description = "Description 2", colorIndex = null),
            )
            whenever(getTasksListUseCase(Config.ITEMS_PER_PAGE, 0)).thenReturn(
                OperationResult.Success(
                    tasksList
                )
            )

            val task = tasksList[0]
            whenever(getTaskUseCase(task.id)).thenReturn(
                OperationResult.Success(null)
            )

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadNext)

            // Assert
            assertThat(viewModel.state.value.tasks).hasSize(2)

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(task.id))

            // Assert
            val updatedTasks = viewModel.state.value.tasks
            assertThat(updatedTasks).hasSize(1)
            assertThat(updatedTasks[0]).isNotEqualTo(task)
        }

    @Test
    fun `handleUIEvent - RefreshTask - when getting task failed shows an error`() =
        runTest {
            // Arrange
            val values = collectInBackground(viewModel.events)

            whenever(getTaskUseCase(1)).thenReturn(
                OperationResult.Fail(OperationError.Unspecified())
            )

            // Act
            viewModel.handleUIEvent(TasksListContract.UIEvent.LoadTask(1))

            // Assert
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TasksListContract.Event.Error::class.java)
        }
}