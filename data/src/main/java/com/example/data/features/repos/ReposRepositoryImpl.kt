package com.example.data.features.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.data.base.State
import com.example.data.features.repos.datasources.FavoriteReposLocalDataSource
import com.example.data.features.repos.datasources.ReposNetworkDataSource
import com.example.data.features.repos.errors.RepoDetailsError
import com.example.data.features.repos.errors.RepoListError
import com.example.data.features.repos.models.apis.RepoApi
import com.example.data.features.repos.models.apis.RepoSortApi
import com.example.data.features.repos.models.business.*
import com.example.data.features.repos.models.locals.RepoLocal
import com.example.data.utils.toLocaleDate
import com.example.data.utils.toLocaleDateTime
import java.time.LocalDateTime
import javax.inject.Inject

class ReposRepositoryImpl @Inject internal constructor(
    private val favoriteReposLocalDataSource: FavoriteReposLocalDataSource,
    private val reposNetworkDataSource: ReposNetworkDataSource
) : ReposRepository {

    private val reposInCaches = mutableListOf<Repo>()
    private val reposInLocalDb = mutableListOf<RepoLocal>()
    private var isFirstTimeObserving = true

    private val _stateRepoList = MutableLiveData(
        State<List<Repo>, RepoListError>(State.Name.IDLE, null, null)
    )
    override val stateRepoList: LiveData<State<List<Repo>, RepoListError>>
        get() = _stateRepoList

    private val _stateRepoDetails = MutableLiveData(
        State<Repo, RepoDetailsError>(State.Name.IDLE, null, null)
    )
    override val stateRepoSelected: LiveData<State<Repo, RepoDetailsError>>
        get() = _stateRepoDetails

    init {
        favoriteReposLocalDataSource.favoriteRepoIdsInLocalDb.observeForever {
            reposInLocalDb.clear()
            reposInLocalDb.addAll(it.orEmpty())
            val result = applyFavoriteTransformation(reposInCaches, reposInLocalDb)
            if (!isFirstTimeObserving) {
                _stateRepoList.value = State(State.Name.SUCCESS, value = result)
            } else {
                isFirstTimeObserving = false
            }
        }
    }

    override suspend fun toggleFavoriteRepo(id: Long, name: String) {
        val isRepoIdInLocalDataDb = favoriteReposLocalDataSource.isRepoExist(id)
        if (isRepoIdInLocalDataDb) {
            favoriteReposLocalDataSource.deleteById(id)
        } else {
            favoriteReposLocalDataSource.insert(RepoLocal(id, name))
        }
    }

    override suspend fun fetchReposList(repoFilter: RepoFilter) {
        try {
            val result = applyFavoriteTransformation(
                fetchRepos(repoFilter),
                reposInLocalDb
            )
            _stateRepoList.value = State(State.Name.SUCCESS, value = result, error = null)
        } catch (t: Throwable) {
            _stateRepoList.value = State(State.Name.ERROR, value = null, error = RepoListError(t))
        }
    }

    override suspend fun forceRefresh() {
        try {
            val result = applyFavoriteTransformation(
                fetchRepos(RepoFilter.newDefaultInstance()),
                reposInLocalDb
            )
            _stateRepoList.value = State(State.Name.SUCCESS, value = result, error = null)
        } catch (t: Throwable) {
            _stateRepoList.value = State(State.Name.ERROR, value = null, error = RepoListError(t))
        }
    }

    override fun selectRepo(id: Long) {
        val selectedRepo = reposInCaches.firstOrNull { it.id == id }
        if (selectedRepo != null) {
            _stateRepoDetails.value =
                State(State.Name.SUCCESS, value = selectedRepo, error = null)
        } else {
            _stateRepoDetails.value =
                State(State.Name.ERROR, value = null, error = RepoDetailsError())
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

    private suspend fun fetchRepos(repoFilter: RepoFilter): List<Repo> {
        _stateRepoList.value = State(State.Name.LOADING)
        val result = reposNetworkDataSource.fetchRepos(
            repoFilter.sort.toRepoSortApi().serverValue,
            repoFilter.perPage,
            repoFilter.query
        )
            .items.map { it.toRepo() }
        result.fillEachRepoWithIssuesOpenedBeforeOneYearAgo()
        reposInCaches.clear()
        reposInCaches.addAll(result)
        return result
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