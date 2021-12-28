package com.example.aircall_test_v2.features.repos.list

import com.example.aircall_test_v2.base.IUiModel

data class RepoItemUiModel(
    val id: Long,
    val name: String,
    val description: String,
    val private: Boolean,
    val createdAt: String,
    val isFavorite: Boolean
) : IUiModel