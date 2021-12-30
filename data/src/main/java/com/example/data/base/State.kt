package com.example.data.base

sealed class State<out T> where T : Any? {
    object Loading : State<Nothing>()
    data class Success<T>(val data: T) : State<T>()
    data class Failure(val t: Throwable) : State<Nothing>()
}