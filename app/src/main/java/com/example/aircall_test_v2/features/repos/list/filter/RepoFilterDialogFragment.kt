package com.example.aircall_test_v2.features.repos.list.filter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseDialogFragment
import com.example.aircall_test_v2.databinding.DialogRepoFilterBinding
import com.example.aircall_test_v2.features.repos.ReposUiAction
import com.example.aircall_test_v2.features.repos.ReposViewModel
import com.google.android.material.radiobutton.MaterialRadioButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepoFilterDialogFragment :
    BaseDialogFragment<DialogRepoFilterBinding, ReposUiAction, ReposViewModel>() {

    override val resId: Int = R.layout.dialog_repo_filter

    override val viewModel: ReposViewModel by hiltNavGraphViewModels(R.id.navigation_repos)

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RepoSortUiModel.values().forEach {
            binding.radioGroupSort.addView(MaterialRadioButton(requireContext())
                .apply {
                    id = it.ordinal
                    setText(it.titleId)
                    buttonTintList = ColorStateList.valueOf(R.color.purple_700)
                })
        }

        binding.buttonValidate.setOnClickListener {
            viewModel.tryChangeFilter()
        }

        binding.radioGroupSort.setOnCheckedChangeListener { _, i ->
            val filterUiModel = viewModel.filterUiModel.value
            viewModel.changeFilterUiModel(filterUiModel?.copy(sort = i))
        }

        binding.editQuery.doAfterTextChanged {
            val filterUiModel = viewModel.filterUiModel.value
            viewModel.changeFilterUiModel(filterUiModel?.copy(query = it.toString()))
        }

        binding.editPerPage.doAfterTextChanged {
            val filterUiModel = viewModel.filterUiModel.value
            viewModel.changeFilterUiModel(filterUiModel?.copy(perPage = it.toString()))
        }
    }

    override fun onReceiveUiAction(action: ReposUiAction?) {
        when (action) {
            is ReposUiAction.ShowErrorPerPageError -> {
                binding.textInputPerPage.error =
                    if (action.showError) getString(R.string.repo_filter_query_error)
                    else null
            }
            is ReposUiAction.ShowErrorQueryError -> {
                binding.textInputQuery.error =
                    if (action.showError) getString(R.string.repo_filter_per_page_error)
                    else null
            }
        }
    }

    companion object {
        fun newInstance(): RepoFilterDialogFragment = RepoFilterDialogFragment()
    }
}
