package com.gmail.umidsafarov.lifebalance.presentation.screen.task

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.umidsafarov.lifebalance.common.model.OperationError
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.usecase.AddTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.RemoveTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.UpdateTaskUseCase
import com.gmail.umidsafarov.lifebalance.presentation.ui.preset.Backgrounds
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TaskViewModel.Factory::class)
class TaskViewModel @AssistedInject constructor(
    @Assisted private val taskId: Long?,
    private val getTaskUseCase: GetTaskUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(taskId: Long?): TaskViewModel
    }

    private val _state =
        mutableStateOf(TaskContract.State(isLoading = true, colorIndex = Backgrounds.getRandom()))
    val state: State<TaskContract.State> = _state

    private val _events = Channel<TaskContract.Event>(capacity = Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            if (taskId == null) {

                _state.value = _state.value.copy(
                    isLoading = false
                )
                return@launch
            }

            when (val result = getTaskUseCase(taskId)) {
                is OperationResult.Success -> {
                    if (result.data == null) {
                        _events.send(TaskContract.Event.Error(OperationError.Validation("Not exist")))
                        navigateBack(null)
                        return@launch
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        title = result.data.title,
                        description = result.data.description,
                        colorIndex = result.data.colorIndex,
                    )
                }

                is OperationResult.Fail -> {
                    _events.send(TaskContract.Event.Error(result.error))
                }
            }
        }
    }

    fun handleUIEvent(event: TaskContract.UIEvent) {
        when (event) {
            is TaskContract.UIEvent.TitleChanged -> titleChanged(event.value)
            is TaskContract.UIEvent.DescriptionChanged -> descriptionChanged(event.value)
            is TaskContract.UIEvent.ColorChanged -> colorChanged(event.value)
            TaskContract.UIEvent.Save -> save()
            TaskContract.UIEvent.Remove -> remove()
        }
    }

    private fun titleChanged(value: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(title = value)
        }
    }

    private fun descriptionChanged(value: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(description = value)
        }
    }

    private fun colorChanged(value: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(colorIndex = value)
        }
    }

    private fun save() {
        viewModelScope.launch {
            if (taskId == null) {
                val resultId = addTask()
                resultId?.let {
                    navigateBack(it)
                }
            } else {
                val result = updateTask()
                if (result) {
                    navigateBack(taskId)
                }
            }
        }
    }

    private suspend fun addTask(): Long? {
        val result = addTaskUseCase(
            title = state.value.title,
            description = state.value.description,
            colorIndex = state.value.colorIndex,
        )

        when (result) {
            is OperationResult.Fail -> {
                _events.send(TaskContract.Event.Error(result.error))
                return null
            }

            is OperationResult.Success -> {
                return result.data
            }
        }
    }

    private suspend fun updateTask(): Boolean {
        val result = updateTaskUseCase(
            Task(
                id = taskId!!,
                title = state.value.title,
                description = state.value.description,
                colorIndex = state.value.colorIndex,
            )
        )

        when (result) {
            is OperationResult.Success -> {
                return true
            }

            is OperationResult.Fail -> {
                _events.send(TaskContract.Event.Error(result.error))
                return false
            }
        }
    }

    private fun remove() {
        viewModelScope.launch {
            if (taskId == null) {
                _events.send(TaskContract.Event.NavigateBack(null))
                return@launch
            }

            val result = removeTaskUseCase(taskId)
            when (result) {
                is OperationResult.Success -> {
                    navigateBack(taskId)
                }

                is OperationResult.Fail -> {
                    _events.send(TaskContract.Event.Error(result.error))
                    return@launch
                }
            }
        }
    }

    private fun navigateBack(resultId: Long?) {
        viewModelScope.launch {
            _events.send(TaskContract.Event.NavigateBack(resultId))
        }
    }
}