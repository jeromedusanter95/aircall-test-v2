package com.example.aircall_test_v2.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.aircall_test_v2.BR
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<B : ViewDataBinding, A : IUiAction, VM : BaseViewModel> :
    AppCompatActivity(), IView<A> {

    abstract val resId: Int

    abstract val viewModel: VM

    lateinit var binding: B

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, resId)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this
        viewModel.action.observe(this, { action ->
            try {
                action?.let {
                    when (it) {
                        is CommonUiAction -> onReceiveCommonUiAction(it)
                        else -> onReceiveUiAction(it as? A)
                    }
                }
            } catch (t: Throwable) {
                if (t !is ClassCastException) t.printStackTrace()
            }
        })
    }

    override fun onReceiveCommonUiAction(action: CommonUiAction) {
        when (action) {
            is CommonUiAction.ShowToast -> Toast.makeText(
                this,
                action.message,
                Toast.LENGTH_SHORT
            ).show()
            is CommonUiAction.ShowSnackBar -> Snackbar.make(
                findViewById(android.R.id.content),
                action.message,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}