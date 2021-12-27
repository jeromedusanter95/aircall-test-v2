package com.example.aircall_test_v2.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.aircall_test_v2.BR
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<B : ViewDataBinding, A : IUiAction, VM : BaseViewModel> : Fragment(),
    IView<A> {

    abstract val resId: Int

    abstract val viewModel: VM

    lateinit var binding: B

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, resId, container, false)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.action.observe(viewLifecycleOwner, { action ->
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
        return binding.root
    }

    fun navigate(navDirections: NavDirections) {
        try {
            findNavController().navigate(navDirections)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun navigate(resId: Int, bundle: Bundle? = null) {
        try {
            findNavController().navigate(resId, bundle)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun popBackStack() {
        try {
            findNavController().popBackStack()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun popBackStack(destinationId: Int, inclusive: Boolean) {
        try {
            findNavController().popBackStack(destinationId, inclusive)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun showKeyboard(context: Context?, view: View?) {
        if (view != null) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.toggleSoftInputFromWindow(view.windowToken, 0, 0);
        }
    }

    private fun hideKeyboard(context: Context?, view: View?) {
        if (view != null) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onReceiveCommonUiAction(action: CommonUiAction) {
        when (action) {
            is CommonUiAction.ShowToast -> Toast.makeText(
                context,
                action.message,
                Toast.LENGTH_SHORT
            ).show()
            is CommonUiAction.ShowSnackBar -> Snackbar.make(
                requireView(),
                action.message,
                Snackbar.LENGTH_SHORT
            ).show()
            is CommonUiAction.NavigateBack -> popBackStack()
            is CommonUiAction.HideKeyBoard -> hideKeyboard(context, view)
            is CommonUiAction.ShowKeyBoard -> showKeyboard(context, view)
        }
    }
}