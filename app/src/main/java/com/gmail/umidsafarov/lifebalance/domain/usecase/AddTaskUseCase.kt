package com.gmail.umidsafarov.lifebalance.domain.usecase

import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: LifeBalanceRepository,
) {
    suspend operator fun invoke(
        title: String?,
        description: String?,
        colorIndex: Int?
    ): OperationResult<Long> {
        return repository.addTask(
            title = title,
            description = description,
            colorIndex = colorIndex
        )
    }
}