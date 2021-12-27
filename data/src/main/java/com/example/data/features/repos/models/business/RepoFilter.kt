package com.example.data.features.repos.models.business

data class RepoFilter(
    val sort: RepoSort,
    val perPage: Int,
    val query: String
) {

    companion object {
        fun newDefaultInstance(): RepoFilter = RepoFilter(RepoSort.STARS, 5, "q")
    }
}