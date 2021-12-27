package com.example.data.features.repos.models.business

import java.time.LocalDate

data class Repo(
    val id: Long,
    val name: String,
    val url: String,
    val owner: String,
    val description: String,
    val private: Boolean,
    val createdAt: LocalDate,
    val watchersCount: Int,
    val stargazersCount: Int,
    val forksCount: Int,
    val issuesHistory: MutableList<IssuesHistoryByWeek>,
    val isFavorite: Boolean
)