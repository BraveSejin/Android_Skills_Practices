package com.sejin.todo_tdd_clean_architecture.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sejin.todo_tdd_clean_architecture.data.entity.ToDoEntity
import com.sejin.todo_tdd_clean_architecture.data.local.db.dao.ToDoDao

@Database(
    entities = [ToDoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase: RoomDatabase() {

    companion object {
        const val DB_NAME = "ToDoDatabase.db"
    }
    abstract fun toDoDao(): ToDoDao

}