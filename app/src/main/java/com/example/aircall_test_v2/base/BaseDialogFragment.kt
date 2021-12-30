package com.example.aircall_test_v2.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.example.aircall_test_v2.BR

abstract class BaseDialogFragment<B : ViewDataBinding, A : IUiAction, VM : BaseViewModel> :
    DialogFragment(), IView<A> {

    abstract val resId: Int

    abstract val viewModel: VM

    lateinit var binding: B

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

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
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
            CommonUiAction.DismissDialog -> dismiss()
            CommonUiAction.HideKeyBoard -> hideKeyboard(context, view)
        }
    }
}