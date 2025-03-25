package com.gmail.umidsafarov.lifebalance.presentation.screen.task

import com.gmail.umidsafarov.lifebalance.common.model.OperationError

class TaskContract {
    data class State(
        val title: String? = null,
        val description: String? = null,
        val colorIndex: Int? = null,
        val isLoading:Boolean = false,
    )

    sealed class Event {
        data class NavigateBack(val resultId: Long?) : Event()
        data class Error(val error: OperationError) : Event()
    }

    sealed class UIEvent {
        data class TitleChanged(val value: String) : UIEvent()
        data class DescriptionChanged(val value: String) : UIEvent()
        data class ColorChanged(val value: Int) : UIEvent()
        data object Save : UIEvent()
        data object Remove : UIEvent()
    }
}