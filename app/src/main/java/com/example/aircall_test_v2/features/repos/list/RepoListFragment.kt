package com.example.aircall_test_v2.features.repos.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseFragment
import com.example.aircall_test_v2.databinding.FragmentRepoListBinding
import com.example.aircall_test_v2.features.repos.ReposUiAction
import com.example.aircall_test_v2.features.repos.ReposViewModel
import com.example.data.AppDatabase
import com.wajahatkarim3.roomexplorer.RoomExplorer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepoListFragment : BaseFragment<FragmentRepoListBinding, ReposUiAction, ReposViewModel>() {

    override val resId: Int = R.layout.fragment_repo_list

    override val viewModel: ReposViewModel by hiltNavGraphViewModels(R.id.navigation_repos)

    private val adapter by lazy { RepoListAdapter(buildRepoAdapterListener()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerRepoGithub.adapter = adapter
        binding.statefulLayoutRepoGithub.setErrorView {
            //buttonRetry.setOnClickListener { viewModel.getRepoGithubList() }
        }
    }

    override fun onReceiveUiAction(action: ReposUiAction?) {
        when (action) {
            is ReposUiAction.NavToRepoGithubDetails -> navigate(RepoListFragmentDirections.actionNavigateToRepoGithubDetails())
            is ReposUiAction.InvalidateOptionsMenu -> requireActivity().invalidateOptionsMenu()
        }
    }

    private fun buildRepoAdapterListener() = object : RepoListAdapter.Listener {
        override fun onClickItem(itemId: Long) {
            viewModel.selectRepoDetails(itemId)
        }

        override fun onFavoriteClick(item: RepoItemUiModel) {
            viewModel.toggleFavorite(item.id, item.name)
        }
    }

    private fun showFilterDialogFragment() {
        //RepoGithubFilterDialogFragment.newInstance().show(childFragmentManager, "")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_repo_list, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemSeeBdd = menu.findItem(R.id.item_see_bdd)
        val itemFilter = menu.findItem(R.id.item_filter)
        val itemRefresh = menu.findItem(R.id.item_refresh)

        viewModel.listUiState.observe(viewLifecycleOwner, { state ->
            itemSeeBdd.isVisible = state != RepoListStatefulLayout.State.LOADING
            itemFilter.isVisible = state != RepoListStatefulLayout.State.LOADING
            itemRefresh.isVisible = state != RepoListStatefulLayout.State.LOADING
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_filter -> {
                showFilterDialogFragment()
                true
            }
            R.id.item_refresh -> {
                viewModel.refreshPage()
                true
            }
            R.id.item_see_bdd -> {
                RoomExplorer.show(context, AppDatabase::class.java, "db-name")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}