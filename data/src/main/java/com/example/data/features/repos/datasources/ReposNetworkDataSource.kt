package com.example.data.features.repos.datasources

import com.example.data.features.repos.apiservice.RepoApiService
import com.example.data.features.repos.models.apis.IssueApi
import com.example.data.features.repos.models.apis.RepoApiResponse
import com.example.data.utils.toDatabaseFormatString
import java.time.LocalDateTime
import javax.inject.Inject

class ReposNetworkDataSource @Inject constructor(private val repoApiService: RepoApiService) {

    suspend fun fetchRepos(sort: String, perPage: Int, query: String): RepoApiResponse {
        return repoApiService.fetchRepositories(
            sort = sort,
            perPage = perPage,
            query = query
        )
    }

    suspend fun getIssuesByRepoGithub(
        owner: String,
        name: String,
        since: LocalDateTime
    ): List<IssueApi> {
        return repoApiService.fetchIssuesByRepo(
            owner,
            name,
            since.toDatabaseFormatString(),
            page = 1,
            perPage = 100
        )
    }
}