package com.example.data.features.repos.datasources

import com.example.data.AppDatabase
import com.example.data.features.repos.models.locals.RepoLocal
import javax.inject.Inject

class FavoriteReposLocalDataSource @Inject internal constructor(private val appDatabase: AppDatabase) {

    val favoriteRepoIdsInLocalDb = appDatabase.repoDao().all()

    suspend fun isRepoExist(repoId: Long): Boolean {
        val count = appDatabase.repoDao().count(repoId)
        return count > 0
    }

    suspend fun deleteById(repoId: Long) {
        appDatabase.repoDao().deleteById(repoId)
    }

    suspend fun insertById(repoId: Long) {
        appDatabase.repoDao().insert(RepoLocal(repoId))
    }
}