package com.example.aircall_test_v2.features.repos.list.filter

import com.example.aircall_test_v2.base.IUiModel

data class RepoFilterUiModel(
    val sort: Int,
    val perPage: String,
    val query: String
) : IUiModel