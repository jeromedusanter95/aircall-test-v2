package com.example.aircall_test_v2.features.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.aircall_test_v2.base.BaseViewModel
import com.example.aircall_test_v2.base.CommonUiAction
import com.example.aircall_test_v2.features.repos.details.RepoDetailsMapper
import com.example.aircall_test_v2.features.repos.details.RepoDetailsUiModel
import com.example.aircall_test_v2.features.repos.list.RepoListStatefulLayout
import com.example.aircall_test_v2.features.repos.list.RepoListUiModel
import com.example.aircall_test_v2.features.repos.list.RepoMapper
import com.example.aircall_test_v2.features.repos.list.filter.RepoFilterMapper
import com.example.aircall_test_v2.features.repos.list.filter.RepoFilterUiModel
import com.example.data.base.State
import com.example.data.features.repos.ReposRepository
import com.example.data.features.repos.models.business.Repo
import com.example.data.features.repos.models.business.RepoFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val repository: ReposRepository,
    private val repoMapper: RepoMapper,
    private val filterMapper: RepoFilterMapper,
    private val repoDetailsMapper: RepoDetailsMapper
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

    private val _filterUiModel =
        MutableLiveData(filterMapper.mapModelToUiModel(RepoFilter.newDefaultInstance()))
    val filterUiModel: LiveData<RepoFilterUiModel>
        get() = _filterUiModel

    private val stateRepoListObserver = Observer<State<List<Repo>>> { state ->
        when (state) {
            is State.Loading -> _listUiState.value = RepoListStatefulLayout.State.LOADING
            is State.Success<List<Repo>> -> {
                state.data.let { list: List<Repo> ->
                    _listUiState.value =
                        if (list.isEmpty()) RepoListStatefulLayout.State.EMPTY
                        else RepoListStatefulLayout.State.CONTENT
                    _listUiModel.value =
                        RepoListUiModel(list.map { repoMapper.mapModelToUiModel(it) })
                }
            }
            is State.Failure -> _listUiState.value = RepoListStatefulLayout.State.ERROR
        }
    }

    private val stateRepoDetailsObserver = Observer<State<Repo>> { state ->
        when (state) {
            is State.Success -> {
                _detailsUiModel.value = repoDetailsMapper.mapModelToUiModel(state.data)
                doUiAction(ReposUiAction.NavToRepoGithubDetails)
            }
            is State.Failure -> {
                doUiAction(CommonUiAction.ShowSnackBar("Dépôt non trouvé"))
            }
        }
    }

    init {
        repository.stateRepoList.observeForever(stateRepoListObserver)
        repository.stateRepoDetails.observeForever(stateRepoDetailsObserver)
        viewModelScope.launch {
            repository.tryFetchRepos()
        }
    }

    fun toggleFavorite(id: Long, name: String) {
        viewModelScope.launch {
            repository.toggleFavoriteRepo(id, name)
        }
    }

    fun changeFilterUiModel(filterUiModel: RepoFilterUiModel?) {
        _filterUiModel.value = filterUiModel
    }

    fun tryChangeFilter() {
        val filterUiModel = filterUiModel.value ?: return
        doUiAction(ReposUiAction.ShowErrorQueryError(filterUiModel.query.isEmpty()))
        doUiAction(ReposUiAction.ShowErrorPerPageError(filterUiModel.perPage.isEmpty() || filterUiModel.perPage.toInt() <= 0))
        if (filterUiModel.query.isNotEmpty() && filterUiModel.perPage.isNotEmpty() && filterUiModel.perPage.toInt() > 0) {
            doUiAction(CommonUiAction.HideKeyBoard)
            doUiAction(CommonUiAction.DismissDialog)
            viewModelScope.launch {
                repository.changeFilter(filterMapper.mapUiModelToModel(filterUiModel))
            }
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

    override fun onCleared() {
        repository.stateRepoList.removeObserver(stateRepoListObserver)
        repository.stateRepoDetails.removeObserver(stateRepoDetailsObserver)
        super.onCleared()
    }
}
