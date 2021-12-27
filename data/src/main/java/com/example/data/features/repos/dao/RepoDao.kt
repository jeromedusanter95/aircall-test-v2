package com.example.data.features.repos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.features.repos.models.locals.RepoLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {

    @Query("SELECT * FROM repos")
    fun all(): Flow<List<RepoLocal>>

    @Query("SELECT * FROM repos WHERE id=:repoId")
    suspend fun getById(repoId: Long): RepoLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repo: RepoLocal)

    @Query("DELETE FROM repos WHERE id=:repoId")
    suspend fun deleteById(repoId: Long)

    @Query("SELECT COUNT() FROM repos WHERE id=:id")
    suspend fun count(id: Long): Int
}