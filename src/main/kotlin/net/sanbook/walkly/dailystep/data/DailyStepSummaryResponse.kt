package net.sanbook.walkly.dailystep.data

import java.time.LocalDate
import java.util.*

data class DailyStepSummaryResponse (
    val accountId: UUID,
    val from: LocalDate,
    val to: LocalDate,
    val daysRecorded: Int,
    val totalSteps: Int,
    val averageSteps: Int,
    val maxDay: DailyStepSummaryDetail?
    )

data class DailyStepSummaryDetail (
    val date: LocalDate,
    val stepCount: Int
)

