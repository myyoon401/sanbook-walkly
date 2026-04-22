package net.sanbook.walkly.dailystep.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class DailyStepResponse(
    val id: Long,
    val accountId: UUID,
    val date: LocalDate,
    val stepCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
