package com.example.aircall_test_v2.features.repos.list.filter

import com.example.data.features.repos.models.business.RepoFilter
import javax.inject.Inject

class RepoFilterMapper @Inject constructor(private val sortMapper: RepoSortMapper) {

    fun mapModelToUiModel(model: RepoFilter): RepoFilterUiModel {
        return RepoFilterUiModel(
            sortMapper.mapModelToUiModel(model.sort).ordinal,
            model.perPage.toString(),
            model.query
        )
    }

    fun mapUiModelToModel(modelFilter: RepoFilterUiModel): RepoFilter {
        return RepoFilter(
            sortMapper.mapUiModelToModel(
                RepoSortUiModel.fromOrdinal(modelFilter.sort) ?: RepoSortUiModel.STARS
            ),
            modelFilter.perPage.toInt(),
            modelFilter.query
        )
    }
}