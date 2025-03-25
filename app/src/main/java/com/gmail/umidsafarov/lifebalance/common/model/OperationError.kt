package com.gmail.umidsafarov.lifebalance.common.model

sealed class OperationError(
) {
    abstract val message: String?

    data class NoConnection(override val message: String? = null) : OperationError()
    data class Unauthorized(override val message: String? = null) : OperationError()
    data class System(override val message: String? = null) : OperationError()
    data class Validation(override val message: String? = null) : OperationError()
    data class Unspecified(override val message: String? = null) : OperationError()
    data class Unknown(override val message: String? = null) : OperationError()
}

fun Throwable.toOperationError(): OperationError {
    //todo convert to specific error
    return OperationError.Unspecified(
        message = message,
    )
}