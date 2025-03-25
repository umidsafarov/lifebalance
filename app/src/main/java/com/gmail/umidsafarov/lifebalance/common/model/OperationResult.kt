package com.gmail.umidsafarov.lifebalance.common.model

sealed class OperationResult<T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Fail<T>(val error: OperationError, val message: String? = null) : OperationResult<T>()
}
