package com.gmail.umidsafarov.lifebalance.domain.usecase

import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val repository: LifeBalanceRepository,
) {
    suspend operator fun invoke(id: Long): OperationResult<Task?> {
        return repository.getTask(id)
    }
}