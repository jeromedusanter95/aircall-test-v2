package com.example.data.features.repos.models.business

import java.time.LocalDateTime

data class IssuesHistoryByWeek(
    var amount: Int,
    val week: Int,
    val weekStartDate: LocalDateTime,
    val weekEndDate: LocalDateTime
)