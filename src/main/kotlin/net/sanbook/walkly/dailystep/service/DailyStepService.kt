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
}
