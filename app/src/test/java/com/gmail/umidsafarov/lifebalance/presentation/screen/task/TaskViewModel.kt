package com.gmail.umidsafarov.lifebalance.presentation.screen.task

import com.gmail.umidsafarov.lifebalance.common.model.OperationError
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.usecase.AddTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.RemoveTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.UpdateTaskUseCase
import com.gmail.umidsafarov.lifebalance.extension.collectInBackground
import com.gmail.umidsafarov.lifebalance.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TaskViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getTaskUseCase: GetTaskUseCase
    private lateinit var addTaskUseCase: AddTaskUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var removeTaskUseCase: RemoveTaskUseCase
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() = runTest {
        getTaskUseCase = mock()
        whenever(getTaskUseCase(1L)).thenReturn(OperationResult.Success(Task(1L, "", "", null)))
        addTaskUseCase = mock()
        updateTaskUseCase = mock()
        removeTaskUseCase = mock()
    }

    @Test
    fun `init - when taskId is null, should not load task and set loading state to false`() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )

            // Assert
            assertThat(viewModel.state.value.isLoading).isFalse()
            verify(getTaskUseCase, never()).invoke(any())
        }

    @Test
    fun `init - when taskId is not null and task exists, should load task and set loading state to false`() =
        runTest {
            // Arrange
            val taskId = 1L
            val task = Task(
                id = taskId,
                title = "Test Task",
                description = "Test Description",
                colorIndex = 123
            )
            whenever(getTaskUseCase(taskId)).thenReturn(OperationResult.Success(task))
            viewModel = TaskViewModel(
                taskId,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )

            // Assert
            assertThat(viewModel.state.value.isLoading).isFalse()
            assertThat(viewModel.state.value.title).isEqualTo("Test Task")
            assertThat(viewModel.state.value.description).isEqualTo("Test Description")
            assertThat(viewModel.state.value.colorIndex).isEqualTo(123)
        }

    @Test
    fun `init - when taskId is not null and task does not exist, should emit error and navigate back with no result`() =
        runTest {
            // Arrange
            val taskId = 1L
            whenever(getTaskUseCase(taskId)).thenReturn(OperationResult.Success(null))
            viewModel = TaskViewModel(
                taskId,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )

            // Act
            val values = collectInBackground(viewModel.events)

            // Assert
            assertThat(values).hasSize(2)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.Error::class.java)
            assertThat((values[0] as TaskContract.Event.Error).error.message).isEqualTo(
                "Not exist"
            )
            assertThat(values[1]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[1] as TaskContract.Event.NavigateBack).resultId).isNull()
        }

    @Test
    fun `init - when getTaskUseCase fails, should emit error`() = runTest {
        // Arrange
        val taskId = 1L
        val errorMessage = "Failed to load task"
        whenever(getTaskUseCase(taskId)).thenReturn(
            OperationResult.Fail(
                OperationError.Unspecified(
                    errorMessage
                )
            )
        )
        viewModel = TaskViewModel(
            taskId,
            getTaskUseCase,
            addTaskUseCase,
            updateTaskUseCase,
            removeTaskUseCase
        )

        // Act
        val values = collectInBackground(viewModel.events)

        // Assert
        assertThat(values).hasSize(1)
        assertThat(values[0]).isInstanceOf(TaskContract.Event.Error::class.java)
        assertThat((values[0] as TaskContract.Event.Error).error.message).isEqualTo(errorMessage)
    }

    @Test
    fun `handleUIEvent - TitleChanged - should update title in state`() = runTest {
        // Arrange
        viewModel = TaskViewModel(
            null,
            getTaskUseCase,
            addTaskUseCase,
            updateTaskUseCase,
            removeTaskUseCase
        )
        val newTitle = "New Title"

        // Act
        viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged(newTitle))

        // Assert
        assertThat(viewModel.state.value.title).isEqualTo(newTitle)
    }

    @Test
    fun `handleUIEvent - DescriptionChanged - should update description in state`() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            val newDescription = "New Description"

            // Act
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged(newDescription))

            // Assert
            assertThat(viewModel.state.value.description).isEqualTo(newDescription)
        }

    @Test
    fun `handleUIEvent - ColorChanged - should update color in state`() = runTest {
        // Arrange
        viewModel = TaskViewModel(
            null,
            getTaskUseCase,
            addTaskUseCase,
            updateTaskUseCase,
            removeTaskUseCase
        )
        val newColor = 456

        // Act
        viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(newColor))

        // Assert
        assertThat(viewModel.state.value.colorIndex).isEqualTo(newColor)
    }

    @Test
    fun `handleUIEvent - Save - when taskId is null, should call addTaskUseCase and navigate back with result`() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Test Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Test Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(789))

            whenever(addTaskUseCase("Test Title", "Test Description", 789)).thenReturn(
                OperationResult.Success(1)
            )

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Save)

            // Assert
            verify(addTaskUseCase).invoke(
                title = "Test Title",
                description = "Test Description",
                colorIndex = 789
            )
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isEqualTo(1)
        }

    @Test
    fun `handleUIEvent - Save - when adding task failed, should show error without navigating back`() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )

            whenever(addTaskUseCase(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
                OperationResult.Fail(
                    OperationError.Unspecified()
                )
            )

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Save)

            // Assert
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.Error::class.java)
        }

    @Test
    fun `handleUIEvent - Save - when taskId is not null, should call updateTaskUseCase and navigate back with correct result`() =
        runTest {
            // Arrange
            val taskId = 1L
            viewModel = TaskViewModel(
                taskId,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Updated Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Updated Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(987))

            whenever(updateTaskUseCase(any())).thenReturn(OperationResult.Success(Unit))

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Save)

            // Assert
            verify(updateTaskUseCase).invoke(
                Task(
                    id = taskId,
                    title = "Updated Title",
                    description = "Updated Description",
                    colorIndex = 987
                )
            )
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isEqualTo(taskId)
        }

    @Test
    fun `handleUIEvent - Save - when updating task failed, should show error without navigating back`() =
        runTest {
            // Arrange
            val taskId = 1L
            viewModel = TaskViewModel(
                taskId,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )

            whenever(updateTaskUseCase(any())).thenReturn(
                OperationResult.Fail(
                    OperationError.Unspecified()
                )
            )

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Save)

            // Assert
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.Error::class.java)
        }


    @Test
    fun `handleUIEvent - Remove - when taskId is not null, should call removeTaskUseCase and navigate back with result`() =
        runTest {
            // Arrange
            val taskId = 1L
            viewModel = TaskViewModel(
                taskId,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Updated Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Updated Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(987))

            whenever(removeTaskUseCase(any())).thenReturn(
                OperationResult.Success(Unit)
            )

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Remove)

            // Assert
            verify(removeTaskUseCase).invoke(
                taskId
            )
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isEqualTo(taskId)
        }

    @Test
    fun `handleUIEvent - Remove - when removing task failed, should show error without navigating back`() =
        runTest {
            // Arrange
            val taskId = 1L
            viewModel = TaskViewModel(
                taskId,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )

            whenever(removeTaskUseCase(any())).thenReturn(
                OperationResult.Fail(
                    OperationError.Unspecified()
                )
            )

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Remove)

            // Assert
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.Error::class.java)
        }


    @Test
    fun `handleUIEvent - Remove - when data changed for new task, should not call addTaskUseCase, UpdateTaskUseCase, removeTaskUseCase and should navigate back with no result`() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Updated Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Updated Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(987))

            // Act
            val values = collectInBackground(viewModel.events)
            viewModel.handleUIEvent(TaskContract.UIEvent.Remove)

            // Assert
            verify(addTaskUseCase, never()).invoke(any(), any(), any())
            verify(updateTaskUseCase, never()).invoke(any())
            verify(removeTaskUseCase, never()).invoke(any())
            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isNull()
        }
}