package com.example.aircall_test_v2.base

interface IUiAction

sealed class CommonUiAction : IUiAction {
    object DismissDialog : CommonUiAction()
    data class ShowToast(val message: String) : CommonUiAction()
    data class ShowSnackBar(val message: String) : CommonUiAction()
    object NavigateBack : CommonUiAction()
    object HideKeyBoard : CommonUiAction()
    object ShowKeyBoard : CommonUiAction()
}
