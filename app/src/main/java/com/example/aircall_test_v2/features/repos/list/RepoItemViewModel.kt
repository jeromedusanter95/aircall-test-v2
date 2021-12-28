package com.example.aircall_test_v2.features.repos.list

import androidx.databinding.ObservableField
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseItemViewModel

class RepoItemViewModel(
    private val listener: RepoListAdapter.Listener?
) : BaseItemViewModel<RepoItemUiModel>() {

    val name = ObservableField("")
    val description = ObservableField("")
    val observableFavoriteIconId = ObservableField(R.drawable.ic_star)

    override fun onItemChanged(item: RepoItemUiModel) {
        name.set(item.name)
        description.set(item.description)
        observableFavoriteIconId.set(if (item.isFavorite) R.drawable.ic_star_selected else R.drawable.ic_star)
    }

    override val onItemClick: (RepoItemUiModel, Int) -> Unit
        get() = { item: RepoItemUiModel, _: Int -> listener?.onClickItem(item.id) }

    fun onFavoriteClick() {
        listener?.onFavoriteClick(item)
    }
}
