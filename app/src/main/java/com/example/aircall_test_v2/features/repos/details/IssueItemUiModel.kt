package com.example.aircall_test_v2.features.repos.details

import com.example.aircall_test_v2.base.IUiModel

data class IssueItemUiModel(
    val week: Int,
    val weekStartDate: String,
    val weekEndDate: String,
    val amount: Int
) : IUiModel