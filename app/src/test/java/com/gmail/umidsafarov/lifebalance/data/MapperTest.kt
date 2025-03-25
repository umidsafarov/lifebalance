package com.gmail.umidsafarov.lifebalance.data

import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import com.gmail.umidsafarov.lifebalance.data.local.helpers.Converter
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapperTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var converter: Converter

    @Before
    fun setup() = runTest {
        converter = Converter()
    }

    @Test
    fun `map from TaskEntity to Task - when valid TaskEntity - should map correctly`() = runTest {
        // Arrange
        val taskEntity = TaskEntity(1, "Test Task", "Test Description", 1)

        // Act
        val result = taskEntity.toTask()

        // Assert
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(taskEntity.id)
        assertThat(result?.title).isEqualTo(taskEntity.title)
        assertThat(result?.description).isEqualTo(taskEntity.description)
        assertThat(result?.colorIndex).isEqualTo(taskEntity.colorIndex)
    }

    @Test
    fun `map from TaskEntity to Task - when TaskEntity id is null - should map to null`() =
        runTest {
            // Arrange
            val taskEntity = TaskEntity(null, "Test Task", "Test Description", 1)

            // Act
            val result = taskEntity.toTask()

            // Assert
            assertThat(result).isNull()
        }

    @Test
    fun `map from Task to TaskEntity - when TaskEntity is valid - should map correctly`() =
        runTest {
            // Arrange
            val task = Task(1, "Test Task", "Test Description", 1)

            // Act
            val result = task.toTaskEntity()

            // Assert
            assertThat(result.id).isEqualTo(task.id)
            assertThat(result.title).isEqualTo(task.title)
            assertThat(result.description).isEqualTo(task.description)
            assertThat(result.colorIndex).isEqualTo(task.colorIndex)
        }
}