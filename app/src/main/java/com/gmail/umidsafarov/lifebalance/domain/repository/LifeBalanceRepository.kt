package com.gmail.umidsafarov.lifebalance.domain.repository

import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.domain.model.Task

interface LifeBalanceRepository {

    suspend fun getTasks(count: Int, offset: Int): OperationResult<List<Task>>
    suspend fun getTask(id: Long): OperationResult<Task?>

    suspend fun addTask(title: String?, description: String?, colorIndex: Int?): OperationResult<Long>
    suspend fun removeTask(id: Long): OperationResult<Unit>
    suspend fun updateTask(task: Task): OperationResult<Unit>

}