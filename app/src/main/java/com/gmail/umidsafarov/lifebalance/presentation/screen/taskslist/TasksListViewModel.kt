package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTaskUseCase
import com.gmail.umidsafarov.lifebalance.domain.usecase.GetTasksListUseCase
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState
import com.gmail.umidsafarov.lifebalance.presentation.Config
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val getTasksListUseCase: GetTasksListUseCase,
    private val getTaskUseCase: GetTaskUseCase,
) : ViewModel() {

    private var tasksList = mutableStateListOf<TaskState>()
    private var getTasksJob: Job? = null

    private val _state =
        mutableStateOf(TasksListContract.State(tasks = tasksList, isLoading = true))
    val state: State<TasksListContract.State> = _state

    private val _events = Channel<TasksListContract.Event>(capacity = Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    init {
        loadNextTasksPage()
    }

    fun handleUIEvent(event: TasksListContract.UIEvent) {
        when (event) {

            TasksListContract.UIEvent.CreateTask -> {
                navigateToTask(null)
            }

            is TasksListContract.UIEvent.TaskChosen -> {
                navigateToTask(event.task.data.id)
            }

            is TasksListContract.UIEvent.TaskSelected -> {
                event.task.isSelected.value = !event.task.isSelected.value
            }

            TasksListContract.UIEvent.LoadNext -> {
                loadNextTasksPage()
            }

            is TasksListContract.UIEvent.LoadTask -> {
                refreshTask(event.id)
            }
        }
    }

    private fun loadNextTasksPage() {
        if (getTasksJob != null && getTasksJob!!.isActive) return
        getTasksJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = getTasksListUseCase(Config.ITEMS_PER_PAGE, tasksList.size)
            when (result) {
                is OperationResult.Success -> {
                    (result.data).let { data ->
                        val filteredData =
                            data.filter { tasksList.indexOfFirst { task -> it.id == task.data.id } == -1 }
                        tasksList.addAll(
                            filteredData.map {
                                TaskState(
                                    data = it,
                                    isSelected = mutableStateOf(false)
                                )
                            })
                    }
                }

                is OperationResult.Fail -> {
                    //todo catch different errors
                    _events.send(TasksListContract.Event.Error(result.error))
                }
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun refreshTask(id: Long) {
        viewModelScope.launch {
            val result = getTaskUseCase(id)
            when (result) {
                is OperationResult.Success -> {

                    val indexOfTask = tasksList.indexOfFirst { it.data.id == id }
                    val isTaskLoaded = indexOfTask != -1

                    if (isTaskLoaded) {
                        if (result.data == null) {
                            tasksList.removeAt(indexOfTask)
                        } else {
                            tasksList[indexOfTask] =
                                TaskState(
                                    result.data,
                                    mutableStateOf(tasksList[indexOfTask].isSelected.value)
                                )
                        }
                    } else {
                        if (result.data == null) return@launch
                        tasksList.add(0, TaskState(result.data, mutableStateOf(false)))
                    }
                }

                is OperationResult.Fail -> {
                    _events.send(TasksListContract.Event.Error(result.error))
                }
            }

        }
    }

    private fun navigateToTask(id: Long?) {
        viewModelScope.launch {
            _events.send(TasksListContract.Event.NavigateToTask(id))
        }
    }

}