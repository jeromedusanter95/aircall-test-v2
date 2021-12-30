package com.example.aircall_test_v2.features.repos.details

import com.example.aircall_test_v2.base.IUiModel

data class RepoDetailsUiModel(
    val name: String,
    val description: String,
    val watchersCount: String,
    val stargazersCount: String,
    val forksCount: String,
    val issueItemList : List<IssueItemUiModel>
) : IUiModel