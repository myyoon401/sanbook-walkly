package net.sanbook.walkly.dailystep.service

import net.sanbook.walkly.account.service.AccountService
import net.sanbook.walkly.dailystep.data.DailyStepResponse
import net.sanbook.walkly.dailystep.data.DailyStepSummaryDetail
import net.sanbook.walkly.dailystep.data.DailyStepSummaryResponse
import net.sanbook.walkly.dailystep.data.UpsertDailyStepRequest
import net.sanbook.walkly.dailystep.entity.DailyStep
import net.sanbook.walkly.dailystep.mapper.toEntity
import net.sanbook.walkly.dailystep.mapper.toResponse
import net.sanbook.walkly.dailystep.repository.DailyStepRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class DailyStepService(
    private val dailyStepRepository: DailyStepRepository,
    private val accountService: AccountService
) {
    @Transactional
    fun upsertDailyStep(request: UpsertDailyStepRequest): DailyStepResponse {
        if (request.stepCount < 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "걸음수는 0보다 작을 수 없습니다.")
        accountService.validateAccountExists(request.accountId)

        val dailyStep = dailyStepRepository.findByAccountIdAndDate(request.accountId, request.date)
            ?.apply { updateStepCount(request.stepCount) }
            ?: request.toEntity()

        return dailyStepRepository.save(dailyStep).toResponse()
    }

    fun getDailyStepByDate(accountId: UUID, date: LocalDate): DailyStepResponse {
        val dailyStep = dailyStepRepository.findByAccountIdAndDate(accountId, date)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "해당사용자의$date 걸음수는 존재하지 않습니다.")
        return dailyStep.toResponse()
    }

    fun getDailyStepSummaryBetween(accountId: UUID, from: LocalDate, to: LocalDate): DailyStepSummaryResponse {
        require(!from.isAfter(to)) { "from은 to보다 늦을 수 없습니다" }
        require(ChronoUnit.DAYS.between(from, to) <= 30) { "최대 31일까지 조회 가능" }

        accountService.validateAccountExists(accountId)

        val dailySteps = dailyStepRepository.findAllByAccountIdAndDateBetween(accountId, from, to)
        val totalSteps: Int = dailySteps.sumOf { it.stepCount }

        return if (dailySteps.isEmpty()) {
            DailyStepSummaryResponse(accountId, from, to, 0, 0, 0, null)
        } else {
            val maxDay = dailySteps.maxByOrNull { it.stepCount }
                ?.let { DailyStepSummaryDetail(it.date, it.stepCount) }
            DailyStepSummaryResponse(
                accountId,
                from,
                to,
                dailySteps.size,
                totalSteps,
                totalSteps / dailySteps.size,
                maxDay
            )
        }
    }
}
