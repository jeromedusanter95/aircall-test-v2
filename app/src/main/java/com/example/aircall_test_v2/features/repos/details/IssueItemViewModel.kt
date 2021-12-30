package com.example.aircall_test_v2.features.repos.details

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.aircall_test_v2.base.BaseItemViewModel

class IssueItemViewModel : BaseItemViewModel<IssueItemUiModel>() {

    val week = ObservableInt()
    val weekStartDate = ObservableField("")
    val weekEndDate = ObservableField("")
    val amount = ObservableInt()

    override fun onItemChanged(item: IssueItemUiModel) {
        week.set(item.week)
        weekStartDate.set(item.weekStartDate)
        weekEndDate.set(item.weekEndDate)
        amount.set(item.amount)
    }

    override val onItemClick: ((IssueItemUiModel, Int) -> Unit)? = null
}