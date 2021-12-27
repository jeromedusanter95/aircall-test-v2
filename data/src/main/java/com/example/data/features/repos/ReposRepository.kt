package com.example.data.features.repos

import com.example.data.features.repos.models.business.Repo
import com.example.data.features.repos.models.business.RepoFilter
import kotlinx.coroutines.flow.Flow

interface ReposRepository {
    val repos: Flow<List<Repo>>
    suspend fun toggleFavoriteRepo(repoId: Long)
    suspend fun fetchReposAndCacheOnSuccess(repoFilter: RepoFilter)
}