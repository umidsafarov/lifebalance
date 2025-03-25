package com.gmail.umidsafarov.lifebalance.data.local

import androidx.test.filters.SmallTest
import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@SmallTest
@HiltAndroidTest
class LifebalanceDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: LifeBalanceDatabase
    private lateinit var dao: LifeBalanceDAO

    private val taskEntities = mutableListOf<TaskEntity>()
    private lateinit var taskEntityWithId: TaskEntity
    private lateinit var newTaskEntity: TaskEntity

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.dao()


        // Arrange
        for (i in 0..10) {
            taskEntities.add(
                TaskEntity(
                    id = i.toLong(),
                    title = "Task $i",
                    description = "Description $i",
                    colorIndex = i,
                )
            )
        }
        taskEntityWithId = taskEntities[0]
        newTaskEntity = TaskEntity(
            id = null,
            title = "New Task",
            description = "New Description",
            colorIndex = null,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTask_whenTaskIsNew_returnsNewTaskId_andReturnsCorrectTask() = runTest {
        // Act
        val addedTaskId = dao.addTask(newTaskEntity)
        val insertedTask = dao.getTask(addedTaskId)

        Truth.assertThat(addedTaskId).isEqualTo(insertedTask?.id)
    }

    @Test
    fun insertTask_whenTaskIsSaved_returnsCorrectTaskId_andReturnsCorrectTask() = runTest {
        // Act
        val addedTaskId = dao.addTask(taskEntityWithId)

        // Assert
        Truth.assertThat(addedTaskId).isEqualTo(taskEntityWithId.id)
    }

    @Test
    fun updateTask_whenTaskIsSaved_updatesDatabase() = runTest {
        // Arrange
        val updatedTitle = "Updated Task"

        // Act
        dao.addTask(taskEntityWithId)
        dao.updateTask(taskEntityWithId.copy(title = updatedTitle))
        val updatedTask = dao.getTask(taskEntityWithId.id!!)

        // Assert
        Truth.assertThat(updatedTask?.title).isEqualTo(updatedTitle)
    }

    @Test
    fun updateTask_whenTaskIsNotSaved_doesNotInsertNewTask() = runTest {
        // Act
        dao.updateTask(taskEntityWithId)
        val updatedTask = dao.getTask(taskEntityWithId.id!!)

        // Assert
        Truth.assertThat(updatedTask).isNull()
    }

    @Test
    fun getTasks_returnsTasks_inReversedOrder() = runTest {
        // Arrange
        for (i in 0 until 3) {
            dao.addTask(taskEntities[i])
        }

        // Act
        val databaseEntities = dao.getTasks(10, 0)

        // Assert
        Truth.assertThat(databaseEntities).hasSize(3)
        Truth.assertThat(databaseEntities[0]).isEqualTo(taskEntities[2])
        Truth.assertThat(databaseEntities[1]).isEqualTo(taskEntities[1])
        Truth.assertThat(databaseEntities[2]).isEqualTo(taskEntities[0])
    }

    @Test
    fun getTasks_whenGettingPartOfTasks_returnsCorrectCount() = runTest {
        // Arrange
        taskEntities.forEach {
            dao.addTask(it)
        }
        val perPageCount = 3

        // Act
        val firstPage = dao.getTasks(perPageCount, 0)

        // Assert
        Truth.assertThat(firstPage).hasSize(perPageCount)
        Truth.assertThat(firstPage[0]).isEqualTo(taskEntities[taskEntities.size - 1])

        // Act
        val secondPage = dao.getTasks(perPageCount, perPageCount)

        // Assert
        Truth.assertThat(secondPage).hasSize(perPageCount)
        Truth.assertThat(secondPage[0])
            .isEqualTo(taskEntities[taskEntities.size - perPageCount - 1])
    }

    @Test
    fun getTasks_whenGettingMoreThanTasksExist_returnsCorrectCount() = runTest {
        // Arrange
        taskEntities.forEach {
            dao.addTask(it)
        }
        val perPageCount = 3

        // Act
        val firstPage = dao.getTasks(perPageCount, 0)

        // Assert
        Truth.assertThat(firstPage).hasSize(perPageCount)
        Truth.assertThat(firstPage[0]).isEqualTo(taskEntities[taskEntities.size - 1])
    }

    @Test
    fun getTasks_returnsEmptyList_whenNoTasksExist() = runTest {
        // Act
        val tasks = dao.getTasks(10, 0)

        // Assert
        Truth.assertThat(tasks).isEmpty()
    }

    @Test
    fun removeTask_removesCorrectTask() = runTest {
        // Arrange
        taskEntities.forEach {
            dao.addTask(it)
        }
        val taskEntityToDelete = taskEntities[3]

        // Act
        dao.deleteTask(taskEntityToDelete.id!!)
        val tasks = dao.getTasks(taskEntities.size, 0)

        // Assert
        Truth.assertThat(tasks).hasSize(taskEntities.size - 1)
        Truth.assertThat(tasks).doesNotContain(taskEntityToDelete)
    }
}