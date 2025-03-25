package com.gmail.umidsafarov.lifebalance.data.repository

import com.gmail.umidsafarov.lifebalance.common.model.OperationError
import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.common.model.toOperationError
import com.gmail.umidsafarov.lifebalance.data.local.LifeBalanceDAO
import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import com.gmail.umidsafarov.lifebalance.data.toTask
import com.gmail.umidsafarov.lifebalance.data.toTaskEntity
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class LifeBalanceRepositoryImpl(
    private val dao: LifeBalanceDAO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LifeBalanceRepository {

    override suspend fun getTasks(
        count: Int,
        offset: Int
    ): OperationResult<List<Task>> {
        runCatching {
            return OperationResult.Success(
                withContext(defaultDispatcher) {
                    dao.getTasks(
                        count,
                        offset
                    )
                }.mapNotNull { it.toTask() })
        }.onFailure {
            return OperationResult.Fail(it.toOperationError())
        }
        return OperationResult.Fail(OperationError.Unknown())
    }

    override suspend fun getTask(id: Long): OperationResult<Task?> {
        runCatching {
            return OperationResult.Success(withContext(defaultDispatcher) {
                dao.getTask(id)?.toTask()
            })
        }.onFailure {
            return OperationResult.Fail(it.toOperationError())
        }
        return OperationResult.Fail(OperationError.Unknown())
    }

    override suspend fun addTask(
        title: String?,
        description: String?,
        colorIndex: Int?
    ): OperationResult<Long> {
        runCatching {
            return OperationResult.Success(
                withContext(defaultDispatcher) {
                    dao.addTask(
                        TaskEntity(
                            id = null,
                            title = title,
                            description = description,
                            colorIndex = colorIndex
                        )
                    )
                }
            )
        }.onFailure {
            return OperationResult.Fail(it.toOperationError())
        }
        return OperationResult.Fail(OperationError.Unknown())
    }

    override suspend fun removeTask(id: Long): OperationResult<Unit> {
        runCatching {
            return OperationResult.Success(
                withContext(defaultDispatcher) { dao.deleteTask(id) }
            )
        }.onFailure {
            return OperationResult.Fail(it.toOperationError())
        }
        return OperationResult.Fail(OperationError.Unknown())
    }

    override suspend fun updateTask(task: Task): OperationResult<Unit> {
        runCatching {
            return OperationResult.Success(
                withContext(defaultDispatcher) { dao.updateTask(task.toTaskEntity()) }
            )
        }.onFailure {
            return OperationResult.Fail(it.toOperationError())
        }
        return OperationResult.Fail(OperationError.Unknown())
    }

}