package com.example.aircall_test_v2.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    val action: LiveData<IUiAction>
        get() = _action
    private val _action: MutableLiveData<IUiAction> = MutableLiveData()

    protected fun doUiAction(action: IUiAction) {
        viewModelScope.launch(Dispatchers.Main) {
            _action.value = action
            _action.value = null
        }
    }
}