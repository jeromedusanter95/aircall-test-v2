package com.example.aircall_test_v2.features.repos.details

import com.example.aircall_test_v2.utils.toFormattedStringWithPattern
import com.example.data.features.repos.models.business.IssuesHistoryByWeek
import javax.inject.Inject

class RepoIssueMapper @Inject constructor() {

    fun mapModelToUiModel(model: IssuesHistoryByWeek): IssueItemUiModel {
        return IssueItemUiModel(
            week = model.week,
            weekStartDate = model.weekStartDate.toFormattedStringWithPattern("dd/MM/yyyy"),
            weekEndDate = model.weekEndDate.toFormattedStringWithPattern("dd/MM/yyyy"),
            amount = model.amount
        )
    }
}