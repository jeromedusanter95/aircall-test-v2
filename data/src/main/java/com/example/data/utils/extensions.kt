package com.example.data.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun LocalDate.toDatabaseFormatString(): String {
    return format(DateTimeFormatter.ISO_DATE_TIME)
}

internal fun LocalDateTime.toDatabaseFormatString(): String {
    return format(DateTimeFormatter.ISO_DATE_TIME)
}

internal fun String.toLocaleDateTime(): LocalDateTime {
    return LocalDateTime.parse(
        this,
        DateTimeFormatter.ISO_DATE_TIME
    )
}

internal fun String.toLocaleDate(): LocalDate {
    return LocalDateTime.parse(
        this,
        DateTimeFormatter.ISO_DATE_TIME
    ).toLocalDate()
}