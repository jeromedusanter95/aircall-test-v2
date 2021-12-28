package com.example.aircall_test_v2.features.repos

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aircall_test_v2.base.BaseViewModel
import com.example.aircall_test_v2.features.repos.list.RepoItemUiModel
import com.example.aircall_test_v2.features.repos.list.RepoListStatefulLayout
import com.example.aircall_test_v2.features.repos.list.RepoListUiModel
import com.example.data.base.State
import com.example.data.features.repos.ReposRepository
import com.example.data.features.repos.models.business.Repo
import com.example.data.features.repos.models.business.RepoFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val repository: ReposRepository
) : BaseViewModel() {

    private val _listUiState = MutableLiveData(RepoListStatefulLayout.State.CONTENT)
    val listUiState: LiveData<RepoListStatefulLayout.State>
        get() = _listUiState
    val listUiModel = ObservableField(RepoListUiModel(emptyList()))

    init {
        repository.stateRepoList.observeForever { state ->
            Log.d("DEBUG", "state : ${state.name}")
            when (state.name) {
                State.Name.LOADING -> _listUiState.value = RepoListStatefulLayout.State.LOADING
                State.Name.LOADED -> {
                    state.value?.let { list ->
                        _listUiState.value =
                            if (list.isEmpty()) RepoListStatefulLayout.State.EMPTY
                            else RepoListStatefulLayout.State.CONTENT
                        listUiModel.set(RepoListUiModel(list.map { it.toRepoUiModel() }))
                    }

                }
                State.Name.ERROR -> _listUiState.value = RepoListStatefulLayout.State.ERROR
                State.Name.IDLE -> Unit
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

    private fun Repo.toRepoUiModel(): RepoItemUiModel {
        return RepoItemUiModel(
            id,
            name,
            description,
            private,
            createdAt.toFormattedStringWithPattern("dd/MM/yyyy"),
            isFavorite
        )
    }

    private fun LocalDate.toFormattedStringWithPattern(pattern: String): String {
        return format(DateTimeFormatter.ofPattern(pattern))
    }
}