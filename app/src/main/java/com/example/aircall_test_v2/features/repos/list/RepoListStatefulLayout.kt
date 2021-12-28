package com.example.aircall_test_v2.features.repos.list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.aircall_test_v2.R
import com.example.aircall_test_v2.databinding.SflRepoGithubListErrorBinding
import cz.kinst.jakub.view.StatefulLayout

class RepoListStatefulLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : StatefulLayout(context, attrs, defStyleAttr) {

    init {
        setStateView(
            State.LOADING.value,
            LayoutInflater.from(getContext()).inflate(
                R.layout.sfl_repo_github_list_loading,
                null
            )
        )

        setStateView(
            State.EMPTY.value,
            LayoutInflater.from(getContext()).inflate(
                R.layout.sfl_repo_github_list_empty,
                null
            )
        )

        setStateView(
            State.ERROR_NO_WIFI.value,
            LayoutInflater.from(getContext()).inflate(
                R.layout.sfl_default_placeholder_offline,
                null
            )
        )
    }

    fun setErrorView(setup: SflRepoGithubListErrorBinding.() -> Unit) {
        val binding = DataBindingUtil.inflate<SflRepoGithubListErrorBinding>(
            LayoutInflater.from(context),
            R.layout.sfl_repo_github_list_error,
            this,
            false
        )
        setStateView(State.ERROR.value, binding.root)
        setup.invoke(binding)
    }

    enum class State constructor(val value: String) {
        CONTENT(StatefulLayout.State.CONTENT),
        ERROR("error"),
        ERROR_NO_WIFI("error_no_wifi"),
        LOADING("loading"),
        EMPTY("empty")
    }
}