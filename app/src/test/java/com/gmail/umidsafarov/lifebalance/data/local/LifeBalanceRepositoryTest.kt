package com.gmail.umidsafarov.lifebalance.data.local

import com.gmail.umidsafarov.lifebalance.common.model.OperationResult
import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import com.gmail.umidsafarov.lifebalance.data.repository.LifeBalanceRepositoryImpl
import com.gmail.umidsafarov.lifebalance.data.toTaskEntity
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import com.gmail.umidsafarov.lifebalance.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LifeBalanceRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var dao: LifeBalanceDAO
    private lateinit var repository: LifeBalanceRepository

    @Before
    fun setup() = runTest {
        dao = mock()
        repository = LifeBalanceRepositoryImpl(dao)
    }

    @Test
    fun `getTasks - when dao returns tasks, should call dao method and return success with mapped tasks`() =
        runTest {
            // Arrange
            val taskEntities = listOf(
                TaskEntity(id = 1, title = "Task 1", description = "Description 1", colorIndex = 1),
                TaskEntity(id = 2, title = "Task 2", description = "Description 2", colorIndex = 2)
            )
            val expectedTasks = listOf(
                Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = 1),
                Task(id = 2, title = "Task 2", description = "Description 2", colorIndex = 2)
            )
            whenever(dao.getTasks(count = 10, offset = 0)).thenReturn(taskEntities)

            // Act
            val result = repository.getTasks(count = 10, offset = 0)

            // Assert
            verify(dao).getTasks(count = 10, offset = 0)
            assertThat(result).isInstanceOf(OperationResult.Success::class.java)
            assertThat((result as OperationResult.Success).data).isEqualTo(expectedTasks)
        }

    @Test
    fun `getTasks - when dao throws exception, call dao method and should return fail`() = runTest {
        // Arrange
        val exception = RuntimeException("Database error")
        whenever(dao.getTasks(count = 10, offset = 0)).thenThrow(exception)

        // Act
        val result = repository.getTasks(count = 10, offset = 0)

        // Assert
        verify(dao).getTasks(count = 10, offset = 0)
        assertThat(result).isInstanceOf(OperationResult.Fail::class.java)
        assertThat((result as OperationResult.Fail).error.message).isEqualTo("Database error")
    }

    @Test
    fun `getTask - when dao returns task, should call dao method and return success with mapped task`() =
        runTest {
            // Arrange
            val taskEntity =
                TaskEntity(id = 1, title = "Task 1", description = "Description 1", colorIndex = 1)
            val expectedTask =
                Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = 1)
            whenever(dao.getTask(1)).thenReturn(taskEntity)

            // Act
            val result = repository.getTask(1)

            // Assert
            verify(dao).getTask(1)
            assertThat(result).isInstanceOf(OperationResult.Success::class.java)
            assertThat((result as OperationResult.Success).data).isEqualTo(expectedTask)
        }

    @Test
    fun `getTask - when dao returns null, should call dao method and return success with null`() =
        runTest {
            // Arrange
            whenever(dao.getTask(1)).thenReturn(null)

            // Act
            val result = repository.getTask(1)

            // Assert
            verify(dao).getTask(id = 1)
            assertThat(result).isInstanceOf(OperationResult.Success::class.java)
            assertThat((result as OperationResult.Success).data).isNull()
        }

    @Test
    fun `getTask - when dao throws exception, should call dao method and return fail`() = runTest {
        // Arrange
        val exception = RuntimeException("Database error")
        whenever(dao.getTask(1)).thenThrow(exception)

        // Act
        val result = repository.getTask(1)

        // Assert
        verify(dao).getTask(1)
        assertThat(result).isInstanceOf(OperationResult.Fail::class.java)
        assertThat((result as OperationResult.Fail).error.message).isEqualTo("Database error")
    }

    @Test
    fun `addTask - when dao adds task, should call dao method and return success with id`() =
        runTest {
            // Arrange
            val taskId = 1L
            whenever(dao.addTask(any())).thenReturn(taskId)

            // Act
            val result =
                repository.addTask(title = "Task 1", description = "Description 1", colorIndex = 1)

            // Assert
            verify(dao).addTask(
                TaskEntity(
                    id = null,
                    title = "Task 1",
                    description = "Description 1",
                    colorIndex = 1
                )
            )
            assertThat(result).isInstanceOf(OperationResult.Success::class.java)
            assertThat((result as OperationResult.Success).data).isEqualTo(taskId)
        }

    @Test
    fun `addTask - when dao throws exception, should call dao method and return fail`() = runTest {
        // Arrange
        val exception = RuntimeException("Database error")
        whenever(dao.addTask(any())).thenThrow(exception)

        // Act
        val result =
            repository.addTask(title = "Task 1", description = "Description 1", colorIndex = 1)

        // Assert
        verify(dao).addTask(
            TaskEntity(
                id = null,
                title = "Task 1",
                description = "Description 1",
                colorIndex = 1
            )
        )
        assertThat(result).isInstanceOf(OperationResult.Fail::class.java)
        assertThat((result as OperationResult.Fail).error.message).isEqualTo("Database error")
    }

    @Test
    fun `removeTask - when dao removes task, should call dao method and return success`() =
        runTest {
            // Act
            val result = repository.removeTask(1)

            // Assert
            verify(dao).deleteTask(1)
            assertThat(result).isInstanceOf(OperationResult.Success::class.java)
        }

    @Test
    fun `removeTask - when dao throws exception, should call dao method and return fail`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Database error")
            whenever(dao.deleteTask(1)).thenThrow(exception)

            // Act
            val result = repository.removeTask(1)

            // Assert
            verify(dao).deleteTask(1)
            assertThat(result).isInstanceOf(OperationResult.Fail::class.java)
            assertThat((result as OperationResult.Fail).error.message).isEqualTo("Database error")
        }

    @Test
    fun `updateTask - when dao updates task, should call dao method and return success`() =
        runTest {
            // Arrange
            val task = Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = 1)

            // Act
            val result = repository.updateTask(task)

            // Assert
            verify(dao).updateTask(task.toTaskEntity())
            assertThat(result).isInstanceOf(OperationResult.Success::class.java)
        }

    @Test
    fun `updateTask - when dao throws exception, should call dao method and return fail`() =
        runTest {
            // Arrange
            val task = Task(id = 1, title = "Task 1", description = "Description 1", colorIndex = 1)
            val exception = RuntimeException("Database error")
            whenever(dao.updateTask(any())).thenThrow(exception)

            // Act
            val result = repository.updateTask(task)

            // Assert
            verify(dao).updateTask(task.toTaskEntity())
            assertThat(result).isInstanceOf(OperationResult.Fail::class.java)
            assertThat((result as OperationResult.Fail).error.message).isEqualTo("Database error")
        }
}
