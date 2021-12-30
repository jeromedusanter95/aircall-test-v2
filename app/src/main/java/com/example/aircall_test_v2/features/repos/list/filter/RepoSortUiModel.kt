package com.example.aircall_test_v2.features.repos.list.filter

import androidx.annotation.StringRes
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.IUiModel

enum class RepoSortUiModel(@StringRes val titleId: Int) : IUiModel {
    STARS(R.string.repo_sort_stars),
    FORKS(R.string.repo_sort_forks),
    HELP_WANTED_ISSUES(R.string.repo_sort_help_wanted_issues),
    UPDATED(R.string.repo_sort_updated);

    companion object {
        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal }
    }
}