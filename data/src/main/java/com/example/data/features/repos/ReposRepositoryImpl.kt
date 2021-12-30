package com.example.data.features.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.data.base.State
import com.example.data.features.repos.datasources.FavoriteReposLocalDataSource
import com.example.data.features.repos.datasources.ReposNetworkDataSource
import com.example.data.features.repos.models.apis.RepoApi
import com.example.data.features.repos.models.apis.RepoSortApi
import com.example.data.features.repos.models.business.*
import com.example.data.features.repos.models.locals.RepoLocal
import com.example.data.utils.toLocaleDate
import com.example.data.utils.toLocaleDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class ReposRepositoryImpl @Inject internal constructor(
    private val favoriteReposLocalDataSource: FavoriteReposLocalDataSource,
    private val reposNetworkDataSource: ReposNetworkDataSource
) : ReposRepository {

    private val reposInCaches = mutableListOf<Repo>()
    private val reposInLocalDb = mutableListOf<RepoLocal>()
    private var repoFilterInCache = RepoFilter.newDefaultInstance()
    private var isFirstTimeObservingLocalDb = true

    private val _stateRepoList = MutableLiveData<State<List<Repo>>>()
    override val stateRepoList: LiveData<State<List<Repo>>>
        get() = _stateRepoList

    private val _stateRepoDetails = MutableLiveData<State<Repo>>()
    override val stateRepoDetails: LiveData<State<Repo>>
        get() = _stateRepoDetails

    init {
        favoriteReposLocalDataSource.favoriteRepoIdsInLocalDb.observeForever {
            reposInLocalDb.clear()
            reposInLocalDb.addAll(it.orEmpty())
            val result = applyFavoriteTransformation(reposInCaches, reposInLocalDb)
            if (!isFirstTimeObservingLocalDb) {
                _stateRepoList.value = State.Success(result)
            } else {
                isFirstTimeObservingLocalDb = false
            }
        }
    }

    override suspend fun toggleFavoriteRepo(id: Long, name: String) {
        withContext(Dispatchers.IO) {
            val isRepoIdInLocalDataDb = favoriteReposLocalDataSource.isRepoExist(id)
            if (isRepoIdInLocalDataDb) {
                favoriteReposLocalDataSource.deleteById(id)
            } else {
                favoriteReposLocalDataSource.insert(RepoLocal(id, name))
            }
        }
    }

    override suspend fun tryFetchRepos() {
        _stateRepoList.value = State.Loading
        try {
            val result = applyFavoriteTransformation(
                fetchRepos(repoFilterInCache),
                reposInLocalDb
            )
            _stateRepoList.value = State.Success(result)
        } catch (t: Throwable) {
            _stateRepoList.value = State.Failure(t)
        }
    }

    override suspend fun forceRefresh() {
        _stateRepoList.value = State.Loading
        try {
            val result = applyFavoriteTransformation(
                fetchRepos(RepoFilter.newDefaultInstance()),
                reposInLocalDb
            )
            _stateRepoList.value = State.Success(result)
        } catch (t: Throwable) {
            _stateRepoList.value = State.Failure(t)
        }
    }

    override fun selectRepo(id: Long) {
        val selectedRepo = reposInCaches.firstOrNull { it.id == id }
        if (selectedRepo != null) {
            _stateRepoDetails.value = State.Success(selectedRepo)
        } else {
            _stateRepoDetails.value = State.Failure(Throwable("Couldn't find repo"))
        }
    }

    override suspend fun changeFilter(repoFilter: RepoFilter) {
        repoFilterInCache = repoFilter
        tryFetchRepos()
    }

    private suspend fun fetchRepos(repoFilter: RepoFilter): List<Repo> {
        return withContext(Dispatchers.IO) {
            val result = reposNetworkDataSource.fetchRepos(
                repoFilter.sort.toRepoSortApi().serverValue,
                repoFilter.perPage,
                repoFilter.query
            )
                .items.map { it.toRepo() }
            result.fillEachRepoWithIssuesOpenedBeforeOneYearAgo()
            reposInCaches.clear()
            reposInCaches.addAll(result)
            result
        }
    }

    private suspend fun List<Repo>.fillEachRepoWithIssuesOpenedBeforeOneYearAgo() {
        this.forEach {
            val issuesHistory = mutableListOf<IssuesHistoryByWeek>()
            var weekStartDate = LocalDateTime.now().minusYears(1)
            var weekEndDate = weekStartDate.plusDays(7)
            for (i in 1 until 52) {
                issuesHistory.add(
                    IssuesHistoryByWeek(
                        0,
                        i,
                        weekStartDate,
                        weekEndDate
                    )
                )
                weekStartDate = weekStartDate.plusDays(7)
                weekEndDate = weekEndDate.plusDays(7)
            }

            val result = reposNetworkDataSource.getIssuesByRepoGithub(
                it.owner,
                it.name,
                LocalDateTime.now().minusYears(1)
            )
                .map { model ->
                    Issue(model.id, model.createdAt.toLocaleDateTime())
                }
            val newList = issuesHistory.map { issuesHistoryByWeek ->
                result.forEach {
                    if (issuesHistoryByWeek.weekStartDate.isBefore(it.createdAt)
                        && issuesHistoryByWeek.weekEndDate.isAfter(it.createdAt)
                    ) {
                        issuesHistoryByWeek.amount++
                    }
                }
                issuesHistoryByWeek
            }.filter { it.amount > 0 }
            it.issuesHistory.addAll(newList)
        }
    }

    private fun applyFavoriteTransformation(
        reposInCache: List<Repo>,
        favoriteRepoIds: List<RepoLocal>
    ): List<Repo> {
        return reposInCache.map { repo ->
            if (favoriteRepoIds.firstOrNull { repo.id == it.id } != null) repo.copy(isFavorite = true)
            else repo.copy(isFavorite = false)
        }
            .sortedByDescending { it.isFavorite }
    }

    private fun RepoApi.toRepo(): Repo {
        return Repo(
            id,
            name,
            url,
            owner = ownerApi.login,
            description.orEmpty(),
            private,
            createdAt.toLocaleDate(),
            watchersCount,
            stargazersCount,
            forksCount,
            mutableListOf(),
            false
        )
    }

    private fun RepoSort.toRepoSortApi(): RepoSortApi {
        return when (this) {
            RepoSort.FORKS -> RepoSortApi.FORKS
            RepoSort.STARS -> RepoSortApi.STARS
            RepoSort.HELP_WANTED_ISSUES -> RepoSortApi.HELP_WANTED_ISSUES
            RepoSort.UPDATED -> RepoSortApi.UPDATED
        }
    }
}