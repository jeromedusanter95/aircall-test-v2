package com.example.aircall_test_v2.features.repos.list

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseFragment
import com.example.aircall_test_v2.base.IUiAction
import com.example.aircall_test_v2.databinding.FragmentRepoListBinding
import com.example.aircall_test_v2.features.repos.ReposViewModel
import dagger.hilt.EntryPoint

@EntryPoint
class RepoListFragment : BaseFragment<FragmentRepoListBinding, IUiAction, ReposViewModel>() {

    override val resId: Int = R.layout.fragment_repo_list

    override val viewModel: ReposViewModel by hiltNavGraphViewModels(R.id.navigation_repos)
}