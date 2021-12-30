package com.example.aircall_test_v2.features.repos.details

import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.base.BaseAdapter
import com.example.aircall_test_v2.databinding.ItemIssueBinding

class IssueListAdapter :
    BaseAdapter<ItemIssueBinding, IssueItemUiModel, IssueItemViewModel>(R.layout.item_issue) {

    override fun createViewModel(): IssueItemViewModel = IssueItemViewModel()
}