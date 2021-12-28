package com.example.aircall_test_v2.utils

internal inline fun <reified T> Any.cast(): T? {
    if (this is T)
        return this
    return null
}