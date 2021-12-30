package com.example.aircall_test_v2.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal inline fun <reified T> Any.cast(): T? {
    if (this is T)
        return this
    return null
}

internal fun LocalDate.toFormattedStringWithPattern(pattern: String): String {
    return format(DateTimeFormatter.ofPattern(pattern))
}

internal fun LocalDateTime.toFormattedStringWithPattern(pattern: String): String {
    return format(DateTimeFormatter.ofPattern(pattern))
}