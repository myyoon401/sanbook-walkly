package net.sanbook.walkly.dailystep.service

import net.sanbook.walkly.account.service.AccountService
import net.sanbook.walkly.dailystep.data.DailyStepResponse
import net.sanbook.walkly.dailystep.data.UpsertDailyStepRequest
import net.sanbook.walkly.dailystep.mapper.toEntity
import net.sanbook.walkly.dailystep.mapper.toResponse
import net.sanbook.walkly.dailystep.repository.DailyStepRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
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

    fun getDailyStepByDate(accountId: UUID, date: LocalDate?): DailyStepResponse {
        val searchDate = date ?: LocalDate.now()
        val dailyStep = dailyStepRepository.findByAccountIdAndDate(accountId, searchDate)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "해당사용자의$searchDate 걸음수는 존재하지 않습니다.")
        return dailyStep.toResponse()
    }
}
