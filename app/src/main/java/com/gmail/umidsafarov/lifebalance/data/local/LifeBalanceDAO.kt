package com.gmail.umidsafarov.lifebalance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import javax.inject.Singleton

@Singleton
@Dao
interface LifeBalanceDAO {

    @Query("SELECT * FROM tasks ORDER BY id DESC LIMIT :count OFFSET :offset")
    fun getTasks(count: Int, offset: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Long): TaskEntity?

    @Insert
    fun addTask(task: TaskEntity) : Long

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTask(id: Long)

    @Update
    fun updateTask(task: TaskEntity)
}