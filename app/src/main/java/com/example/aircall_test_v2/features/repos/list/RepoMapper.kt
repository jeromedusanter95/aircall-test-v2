package com.example.aircall_test_v2.features.repos.list

import com.example.aircall_test_v2.utils.toFormattedStringWithPattern
import com.example.data.features.repos.models.business.Repo
import javax.inject.Inject

class RepoMapper @Inject constructor() {
    fun mapModelToUiModel(model: Repo): RepoItemUiModel {
        return RepoItemUiModel(
            model.id,
            model.name,
            model.description,
            model.private,
            model.createdAt.toFormattedStringWithPattern("dd/MM/yyyy"),
            model.isFavorite
        )
    }
}