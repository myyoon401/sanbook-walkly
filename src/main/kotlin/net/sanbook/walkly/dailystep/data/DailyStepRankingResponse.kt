package net.sanbook.walkly.dailystep.data

import net.sanbook.walkly.dailystep.type.Period
import java.time.LocalDate
import java.util.UUID

data class DailyStepRankingResponse(
    val period: Period,
    val from: LocalDate,
    val to: LocalDate,
    val rankings: List<DailyStepRanking>
)

data class DailyStepRanking(
    var rank: Int = 0,
    val accountId: UUID,
    val nickname: String,
    val totalSteps: Int
)
