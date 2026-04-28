package net.sanbook.walkly.dailystep.mapper

import net.sanbook.walkly.dailystep.data.DailyStepResponse
import net.sanbook.walkly.dailystep.data.DailyStepRanking
import net.sanbook.walkly.dailystep.data.UpsertDailyStepRequest
import net.sanbook.walkly.dailystep.entity.DailyStep

fun UpsertDailyStepRequest.toEntity(): DailyStep = DailyStep(
    accountId = accountId,
    date = date,
    stepCount = stepCount
)

fun DailyStep.toResponse(): DailyStepResponse = DailyStepResponse(
    id = id,
    accountId = accountId,
    date = date,
    stepCount = stepCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun DailyStep.toRanking(): DailyStepRanking = DailyStepRanking(
    accountId = accountId,
    totalSteps = stepCount
)
