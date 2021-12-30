package com.example.aircall_test_v2.features.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aircall_test_v2.base.BaseViewModel
import com.example.aircall_test_v2.base.CommonUiAction
import com.example.aircall_test_v2.features.repos.details.IssueItemUiModel
import com.example.aircall_test_v2.features.repos.details.RepoDetailsUiModel
import com.example.aircall_test_v2.features.repos.list.RepoItemUiModel
import com.example.aircall_test_v2.features.repos.list.RepoListStatefulLayout
import com.example.aircall_test_v2.features.repos.list.RepoListUiModel
import com.example.data.base.State
import com.example.data.features.repos.ReposRepository
import com.example.data.features.repos.models.business.IssuesHistoryByWeek
import com.example.data.features.repos.models.business.Repo
import com.example.data.features.repos.models.business.RepoFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val repository: ReposRepository
) : BaseViewModel() {

    private val _listUiState = MutableLiveData(RepoListStatefulLayout.State.CONTENT)
    val listUiState: LiveData<RepoListStatefulLayout.State>
        get() = _listUiState

    private val _listUiModel = MutableLiveData<RepoListUiModel>(null)
    val listUiModel: LiveData<RepoListUiModel>
        get() = _listUiModel

    private val _detailsUiModel = MutableLiveData<RepoDetailsUiModel>(null)
    val detailsUiModel: LiveData<RepoDetailsUiModel>
        get() = _detailsUiModel

    init {
        repository.stateRepoList.observeForever { state ->
            when (state.name) {
                State.Name.LOADING -> _listUiState.value = RepoListStatefulLayout.State.LOADING
                State.Name.SUCCESS -> {
                    state.value?.let { list ->
                        _listUiState.value =
                            if (list.isEmpty()) RepoListStatefulLayout.State.EMPTY
                            else RepoListStatefulLayout.State.CONTENT
                        _listUiModel.value = RepoListUiModel(list.map { it.toRepoItemUiModel() })
                    }

                }
                State.Name.ERROR -> _listUiState.value = RepoListStatefulLayout.State.ERROR
                State.Name.IDLE -> Unit
            }
        }

        repository.stateRepoSelected.observeForever { state ->
            when (state.name) {
                State.Name.SUCCESS -> {
                    _detailsUiModel.value = state.value?.toRepoDetailsUiModel()
                    doUiAction(ReposUiAction.NavToRepoGithubDetails)
                }
                State.Name.ERROR -> {
                    doUiAction(CommonUiAction.ShowSnackBar("Dépôt non trouvé"))
                }
            }
        }

        viewModelScope.launch {
            repository.fetchReposList(RepoFilter.newDefaultInstance())
        }
    }

    fun toggleFavorite(id: Long, name: String) {
        viewModelScope.launch {
            repository.toggleFavoriteRepo(id, name)
        }
    }

    fun changeFilter(repoFilter: RepoFilter) {
        viewModelScope.launch {
            repository.fetchReposList(repoFilter)
        }
    }

    fun refreshPage() {
        viewModelScope.launch {
            repository.forceRefresh()
        }
    }

    fun selectRepoDetails(id: Long) {
        viewModelScope.launch {
            repository.selectRepo(id)
        }
    }

    private fun Repo.toRepoItemUiModel(): RepoItemUiModel {
        return RepoItemUiModel(
            id,
            name,
            description,
            private,
            createdAt.toFormattedStringWithPattern("dd/MM/yyyy"),
            isFavorite
        )
    }

    private fun Repo.toRepoDetailsUiModel(): RepoDetailsUiModel {
        return RepoDetailsUiModel(
            name,
            description,
            watchersCount.toString(),
            stargazersCount.toString(),
            forksCount.toString(),
            issuesHistory.map { it.toIssueUiModel() }
        )
    }

    private fun IssuesHistoryByWeek.toIssueUiModel(): IssueItemUiModel {
        return IssueItemUiModel(
            week = week,
            weekStartDate = weekStartDate.toFormattedStringWithPattern("dd/MM/yyyy"),
            weekEndDate = weekEndDate.toFormattedStringWithPattern("dd/MM/yyyy"),
            amount = amount
        )
    }

    private fun LocalDate.toFormattedStringWithPattern(pattern: String): String {
        return format(DateTimeFormatter.ofPattern(pattern))
    }

    private fun LocalDateTime.toFormattedStringWithPattern(pattern: String): String {
        return format(DateTimeFormatter.ofPattern(pattern))
    }
}
