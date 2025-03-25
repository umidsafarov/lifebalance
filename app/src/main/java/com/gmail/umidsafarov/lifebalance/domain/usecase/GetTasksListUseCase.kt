package com.gmail.umidsafarov.lifebalance.domain.usecase

import android.util.Log
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import org.jetbrains.annotations.Debug
import javax.inject.Inject

class GetTasksListUseCase @Inject constructor(
    private val repository: LifeBalanceRepository,
) {
    suspend operator fun invoke(pageSize: Int, offset: Int): OperationResult<List<Task>> {
        return repository.getTasks(pageSize, offset)
    }
}