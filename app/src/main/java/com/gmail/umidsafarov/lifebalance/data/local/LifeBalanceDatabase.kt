package com.gmail.umidsafarov.lifebalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gmail.umidsafarov.lifebalance.data.local.entitites.TaskEntity
import com.gmail.umidsafarov.lifebalance.data.local.helpers.Converter

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converter::class)
abstract class LifeBalanceDatabase : RoomDatabase() {
    abstract fun dao(): LifeBalanceDAO
}