package com.example.aircall_test_v2.features.repos

import com.example.aircall_test_v2.base.IUiAction

sealed class ReposUiAction : IUiAction {
    object NavToRepoGithubDetails : ReposUiAction()
    object InvalidateOptionsMenu : ReposUiAction()
    data class ShowErrorQueryError(val showError: Boolean) : ReposUiAction()
    data class ShowErrorPerPageError(val showError: Boolean) : ReposUiAction()
}