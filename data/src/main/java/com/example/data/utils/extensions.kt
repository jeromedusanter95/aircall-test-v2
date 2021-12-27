package com.example.data.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
internal fun LocalDate.toDatabaseFormatString(): String {
    return format(DateTimeFormatter.ISO_DATE_TIME)
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun LocalDateTime.toDatabaseFormatString(): String {
    return format(DateTimeFormatter.ISO_DATE_TIME)
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun String.toLocaleDateTime(): LocalDateTime {
    return LocalDateTime.parse(
        this,
        DateTimeFormatter.ISO_DATE_TIME
    )
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun String.toLocaleDate(): LocalDate {
    return LocalDateTime.parse(
        this,
        DateTimeFormatter.ISO_DATE_TIME
    ).toLocalDate()
}