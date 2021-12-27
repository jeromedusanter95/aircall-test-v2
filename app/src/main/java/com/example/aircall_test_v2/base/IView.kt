package com.example.aircall_test_v2.base

interface IView<A> {
    fun onReceiveUiAction(action: A?) = Unit
    fun onReceiveCommonUiAction(action: CommonUiAction)
}