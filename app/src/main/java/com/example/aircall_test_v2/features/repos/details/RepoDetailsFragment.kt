package com.example.aircall_test_v2.features.repos.details

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseFragment
import com.example.aircall_test_v2.base.CommonUiAction
import com.example.aircall_test_v2.base.IUiAction
import com.example.aircall_test_v2.databinding.FragmentRepoDetailsBinding
import com.example.aircall_test_v2.features.repos.ReposViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepoDetailsFragment : BaseFragment<FragmentRepoDetailsBinding, IUiAction, ReposViewModel>() {

    override val resId: Int = R.layout.fragment_repo_details

    override val viewModel: ReposViewModel by hiltNavGraphViewModels(R.id.navigation_repos)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerIssues.adapter = IssueListAdapter()
    }
}