package com.example.data.features.repos.apiservice

import com.example.data.features.repos.models.apis.IssueApi
import com.example.data.features.repos.models.apis.RepoApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RepoApiService {

    @GET("search/repositories")
    suspend fun fetchRepositories(
        @Query("sort") sort: String?,
        @Query("order") order: String? = "desc",
        @Query("page") page: Int? = 1,
        @Query("per_page") perPage: Int,
        @Query("q") query: String
    ): RepoApiResponse

    @GET("repos/{owner}/{repo}/issues")
    suspend fun fetchIssuesByRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("since") since: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<IssueApi>
}