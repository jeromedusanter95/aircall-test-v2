package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.features.repos.dao.RepoDao
import com.example.data.features.repos.models.locals.RepoLocal

@Database(entities = [RepoLocal::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}