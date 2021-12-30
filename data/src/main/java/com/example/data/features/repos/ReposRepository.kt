package com.example.data.features.repos

import androidx.lifecycle.LiveData
import com.example.data.base.State
import com.example.data.features.repos.errors.RepoDetailsError
import com.example.data.features.repos.errors.RepoListError
import com.example.data.features.repos.models.business.Repo
import com.example.data.features.repos.models.business.RepoFilter

interface ReposRepository {
    val stateRepoList: LiveData<State<List<Repo>, RepoListError>>
    val stateRepoDetails: LiveData<State<Repo, RepoDetailsError>>
    suspend fun toggleFavoriteRepo(id: Long, name: String)
    suspend fun tryFetchRepos()
    suspend fun forceRefresh()
    fun selectRepo(id: Long)
    suspend fun changeFilter(repoFilter: RepoFilter)
}