package com.example.data.features.repos.models.apis

import com.google.gson.annotations.SerializedName

data class RepoApi(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("html_url") val url: String,
    @SerializedName("owner") val ownerApi: RepoOwnerApi,
    @SerializedName("description") val description: String?,
    @SerializedName("private") val private: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("watchers_count") val watchersCount: Int,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("forks_count") val forksCount: Int,
    @SerializedName("open_issues") val openIssues: Int
)