package com.example.data.features.repos.models.apis

import com.google.gson.annotations.SerializedName

data class RepoApiResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("incomplete_results") val inCompleteResults: Boolean,
    @SerializedName("items") val items: List<RepoApi>
)