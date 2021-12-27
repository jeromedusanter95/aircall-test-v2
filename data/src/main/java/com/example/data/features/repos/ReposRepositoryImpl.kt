package com.example.data.features.repos

import com.example.data.features.repos.datasources.FavoriteReposLocalDataSource
import com.example.data.features.repos.datasources.ReposNetworkDataSource
import com.example.data.features.repos.models.apis.RepoApi
import com.example.data.features.repos.models.apis.RepoSortApi
import com.example.data.features.repos.models.business.*
import com.example.data.features.repos.models.locals.RepoLocal
import com.example.data.utils.CacheOnSuccess
import com.example.data.utils.toLocaleDate
import com.example.data.utils.toLocaleDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class ReposRepositoryImpl @Inject internal constructor(
    private val favoriteReposLocalDataSource: FavoriteReposLocalDataSource,
    private val reposNetworkDataSource: ReposNetworkDataSource
) : ReposRepository {

    private lateinit var reposInCache: CacheOnSuccess<List<Repo>>

    override val repos: Flow<List<Repo>> = flow {
        emit(reposInCache.getOrAwait())
    }
        .combine(favoriteReposLocalDataSource.favoriteRepoIdsInLocalDb) { reposInCache: List<Repo>, favoriteRepoIds: List<RepoLocal> ->
            reposInCache.map { repo ->
                if (favoriteRepoIds.firstOrNull { repo.id == it.id } != null) repo.copy(isFavorite = true)
                else repo.copy(isFavorite = false)
            }
                .sortedByDescending { it.isFavorite }
        }

    override suspend fun toggleFavoriteRepo(repoId: Long) {
        val isRepoIdInLocalDataDb = favoriteReposLocalDataSource.isRepoExist(repoId)
        if (isRepoIdInLocalDataDb) {
            favoriteReposLocalDataSource.deleteById(repoId)
        } else {
            favoriteReposLocalDataSource.insertById(repoId)
        }
    }

    override suspend fun fetchReposAndCacheOnSuccess(repoFilter: RepoFilter) {
        reposInCache = CacheOnSuccess(onErrorFallback = { emptyList() }, {
            val result = reposNetworkDataSource.fetchRepos(
                sort = repoFilter.sort.toRepoSortApi().serverValue,
                perPage = repoFilter.perPage,
                query = repoFilter.query
            )
                .items.map { it.toRepo() }
            result.apply { fillEachRepoWithIssuesOpenedBeforeOneYearAgo() }
        })
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