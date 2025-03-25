package com.gmail.umidsafarov.lifebalance.presentation.screen.task

import androidx.test.filters.SmallTest
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.usecase.AddTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.RemoveTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.UpdateTaskUseCase
import com.gmail.umidsafarov.lifebalance.extension.collectInBackground
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
class TaskViewModelTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var getTaskUseCase: GetTaskUseCase

    @Inject
    lateinit var addTaskUseCase: AddTaskUseCase

    @Inject
    lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Inject
    lateinit var removeTaskUseCase: RemoveTaskUseCase

    private lateinit var viewModel: TaskViewModel

    private lateinit var savedTask: Task

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
    }

    private suspend fun initSavedTask() {
        savedTask = Task(1, "Saved Task", "Saved task description", 1)
        addTaskUseCase(savedTask.title, savedTask.description, savedTask.colorIndex)
        viewModel = TaskViewModel(
            savedTask.id,
            getTaskUseCase,
            addTaskUseCase,
            updateTaskUseCase,
            removeTaskUseCase
        )
    }

    @Test
    fun supportFunctionsWorkCorrectly() = runTest {
        // Act
        val addResult = addTaskUseCase("Task 1", null, null)

        // Assert
        assertThat(addResult).isInstanceOf(OperationResult.Success::class.java)

        // Act
        val taskId = (addResult as? OperationResult.Success)?.data

        // Assert
        assertThat(taskId).isNotNull()
        taskId ?: return@runTest

        // Act
        val initialResult = getTaskUseCase(taskId)

        // Assert
        assertThat(initialResult).isInstanceOf(OperationResult.Success::class.java)
        assertThat((initialResult as OperationResult.Success).data?.title).isEqualTo("Task 1")

        // Act
        updateTaskUseCase(Task(taskId, "Task 2", null, null))
        val updatedResult = getTaskUseCase(taskId)

        // Assert
        assertThat(updatedResult).isInstanceOf(OperationResult.Success::class.java)
        assertThat((updatedResult as? OperationResult.Success)?.data?.title).isEqualTo("Task 2")
    }

    @Test
    fun init_whenTaskIdIsNull_shouldNotLoadTaskAndSetLoadingStateToFalse() = runTest {
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
        assertThat(viewModel.state.value.title).isNull()
    }

    @Test
    fun init_whenTaskIdIsNotNullAndTaskExists_shouldLoadTaskAndSetLoadingStateToFalse() =
        runTest {
            // Arrange
            initSavedTask()

            // Assert
            assertThat(viewModel.state.value.isLoading).isFalse()
            assertThat(viewModel.state.value.title).isEqualTo(savedTask.title)
            assertThat(viewModel.state.value.description).isEqualTo(savedTask.description)
            assertThat(viewModel.state.value.colorIndex).isEqualTo(savedTask.colorIndex)
        }

    @Test
    fun handleUIEvent_TitleChanged_shouldUpdateTitleInState() = runTest {
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
    fun handleUIEvent_DescriptionChanged_shouldUpdateDescriptionInState() =
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
    fun handleUIEvent_ColorChanged_shouldUpdateColorInState() = runTest {
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
    fun handleUIEvent_Save_whenTaskIdIsNull_shouldAddTaskAndNavigateBackWithResult() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            val values = collectInBackground(viewModel.events)

            // Act
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Test Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Test Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(789))
            viewModel.handleUIEvent(TaskContract.UIEvent.Save)

            val addTaskResult = getTaskUseCase(1L)

            // Assert
            assertThat(addTaskResult).isInstanceOf(OperationResult.Success::class.java)
            val addedTask = (addTaskResult as OperationResult.Success).data
            assertThat(addedTask).isNotNull()
            addedTask ?: return@runTest

            assertThat(addedTask.title).isEqualTo("Test Title")
            assertThat(addedTask.description).isEqualTo("Test Description")
            assertThat(addedTask.colorIndex).isEqualTo(789)

            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isEqualTo(1)
        }

    @Test
    fun handleUIEvent_Save_whenTaskIdIsNotNull_shouldCallUpdateTaskUseCaseAndNavigateBackWithCorrectResult() =
        runTest {
            // Arrange
            initSavedTask()
            val values = collectInBackground(viewModel.events)

            // Act
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Updated Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Updated Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(987))
            viewModel.handleUIEvent(TaskContract.UIEvent.Save)

            val updatedTaskResult = getTaskUseCase(savedTask.id)

            // Assert
            assertThat(updatedTaskResult).isInstanceOf(OperationResult.Success::class.java)
            val updatedTask = (updatedTaskResult as OperationResult.Success).data
            assertThat(updatedTask).isNotNull()
            updatedTask ?: return@runTest

            assertThat(updatedTask.title).isEqualTo("Updated Title")
            assertThat(updatedTask.description).isEqualTo("Updated Description")
            assertThat(updatedTask.colorIndex).isEqualTo(987)

            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isEqualTo(
                savedTask.id
            )
        }

    @Test
    fun handleUIEvent_Remove_whenTaskIdIsNotNull_shouldRemoveTaskAndNavigateBackWithResult() =
        runTest {
            // Arrange
            initSavedTask()
            val values = collectInBackground(viewModel.events)

            // Act
            viewModel.handleUIEvent(TaskContract.UIEvent.Remove)

            val updatedTaskResult = getTaskUseCase(savedTask.id)

            // Assert
            assertThat(updatedTaskResult).isInstanceOf(OperationResult.Success::class.java)
            val updatedTask = (updatedTaskResult as OperationResult.Success).data
            assertThat(updatedTask).isNull()

            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isEqualTo(savedTask.id)
        }

    @Test
    fun handleUIEvent_Remove_whenIdIsNullAndDataChanged_shouldNotAddTaskAndShouldNavigateBackWithNoResult() =
        runTest {
            // Arrange
            viewModel = TaskViewModel(
                null,
                getTaskUseCase,
                addTaskUseCase,
                updateTaskUseCase,
                removeTaskUseCase
            )
            val values = collectInBackground(viewModel.events)

            // Act
            viewModel.handleUIEvent(TaskContract.UIEvent.TitleChanged("Updated Title"))
            viewModel.handleUIEvent(TaskContract.UIEvent.DescriptionChanged("Updated Description"))
            viewModel.handleUIEvent(TaskContract.UIEvent.ColorChanged(987))
            viewModel.handleUIEvent(TaskContract.UIEvent.Remove)


            val shouldNotExistTaskResult = getTaskUseCase(1L)

            // Assert
            assertThat(shouldNotExistTaskResult).isInstanceOf(OperationResult.Success::class.java)
            val shouldNotExistTask = (shouldNotExistTaskResult as OperationResult.Success).data
            assertThat(shouldNotExistTask).isNull()

            assertThat(values).hasSize(1)
            assertThat(values[0]).isInstanceOf(TaskContract.Event.NavigateBack::class.java)
            assertThat((values[0] as TaskContract.Event.NavigateBack).resultId).isNull()
        }
}