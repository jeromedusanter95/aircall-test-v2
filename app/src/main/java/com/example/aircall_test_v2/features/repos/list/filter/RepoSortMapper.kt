package com.example.aircall_test_v2.features.repos.list.filter

import com.example.data.features.repos.models.business.RepoSort
import javax.inject.Inject

class RepoSortMapper @Inject constructor() {

    fun mapModelToUiModel(model: RepoSort): RepoSortUiModel {
        return when (model) {
            RepoSort.FORKS -> RepoSortUiModel.FORKS
            RepoSort.STARS -> RepoSortUiModel.STARS
            RepoSort.HELP_WANTED_ISSUES -> RepoSortUiModel.HELP_WANTED_ISSUES
            RepoSort.UPDATED -> RepoSortUiModel.UPDATED
        }
    }

    fun mapUiModelToModel(model: RepoSortUiModel): RepoSort {
        return when (model) {
            RepoSortUiModel.FORKS -> RepoSort.FORKS
            RepoSortUiModel.STARS -> RepoSort.STARS
            RepoSortUiModel.HELP_WANTED_ISSUES -> RepoSort.HELP_WANTED_ISSUES
            RepoSortUiModel.UPDATED -> RepoSort.UPDATED
        }
    }
}