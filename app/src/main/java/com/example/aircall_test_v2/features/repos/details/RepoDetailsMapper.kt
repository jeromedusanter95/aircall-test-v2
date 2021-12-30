package com.example.aircall_test_v2.features.repos.details

import com.example.data.features.repos.models.business.Repo
import javax.inject.Inject

class RepoDetailsMapper @Inject constructor(private val issueMapper: RepoIssueMapper) {

    fun mapModelToUiModel(model: Repo): RepoDetailsUiModel {
        return RepoDetailsUiModel(
            model.name,
            model.description,
            model.watchersCount.toString(),
            model.stargazersCount.toString(),
            model.forksCount.toString(),
            model.issuesHistory.map { issueMapper.mapModelToUiModel(it) }
        )
    }
}