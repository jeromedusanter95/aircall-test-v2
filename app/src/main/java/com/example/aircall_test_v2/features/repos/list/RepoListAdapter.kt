package com.example.aircall_test_v2.features.repos.list

import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseAdapter
import com.example.aircall_test_v2.databinding.ItemRepoBinding

class RepoListAdapter(
    private val listener: Listener? = null
) : BaseAdapter<ItemRepoBinding, RepoItemUiModel, RepoItemViewModel>(
    R.layout.item_repo
) {
    override fun createViewModel(): RepoItemViewModel = RepoItemViewModel(listener)

    interface Listener {
        fun onClickItem(itemId: Long)
        fun onFavoriteClick(item: RepoItemUiModel)
    }
}