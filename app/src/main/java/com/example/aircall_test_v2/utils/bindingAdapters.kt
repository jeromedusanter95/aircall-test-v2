package com.example.aircall_test_v2.utils

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.aircall_test_v2.base.BaseItemViewModel
import com.example.aircall_test_v2.base.BaseAdapter
import com.example.aircall_test_v2.base.IUiModel
import cz.kinst.jakub.view.StatefulLayout

@BindingAdapter("app:list")
internal fun <T> setList(view: RecyclerView, list: List<T>) {
    val adapter = view.adapter as? BaseAdapter<ViewDataBinding, IUiModel, BaseItemViewModel<IUiModel>>
    adapter?.addItems(list as List<IUiModel>, clear = true)
}

@BindingAdapter("app:uiState")
internal fun setUiState(view: StatefulLayout, state: String) {
    view.state = state
}

@BindingAdapter("app:visibleOrGone")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}