package com.example.aircall_test_v2

import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.aircall_test_v2.base.BaseActivity
import com.example.aircall_test_v2.base.IUiAction
import com.example.aircall_test_v2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, IUiAction, MainViewModel>() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val appBarConfiguration by lazy { AppBarConfiguration(setOf(R.id.fragment_repo_list)) }

    override val resId: Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp() = NavigationUI.navigateUp(navController, appBarConfiguration)
}