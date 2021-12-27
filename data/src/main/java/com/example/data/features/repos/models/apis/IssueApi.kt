package com.example.data.features.repos.models.apis

import com.google.gson.annotations.SerializedName

data class IssueApi(
    @SerializedName("id ") val id: Int,
    @SerializedName("created_at") val createdAt: String
)