package com.example.data.base

data class State<T, E>(
    val name: Name,
    val value: T? = null,
    val error: E? = null
) {
    enum class Name {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }
}