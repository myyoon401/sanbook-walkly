package net.sanbook.walkly.dailystep.data

import java.time.LocalDate
import java.util.UUID

data class UpsertDailyStepRequest(
    val accountId: UUID,
    val date: LocalDate,
    val stepCount: Int
)
