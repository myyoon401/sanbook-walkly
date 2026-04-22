package net.sanbook.walkly.dailystep.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.sanbook.walkly.dailystep.data.DailyStepResponse
import net.sanbook.walkly.dailystep.data.UpsertDailyStepRequest
import net.sanbook.walkly.dailystep.service.DailyStepService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "DailyStep", description = "일일 걸음수 기록")
@RestController
class DailyStepController(
    private val dailyStepService: DailyStepService
) {
    @Operation(summary = "걸음수 Upsert", description = "같은 (accountId, date) 레코드가 있으면 stepCount를 덮어쓰고, 없으면 새로 생성한다")
    @PostMapping("/daily-steps")
    fun upsertDailyStep(@RequestBody request: UpsertDailyStepRequest): DailyStepResponse {
        return dailyStepService.upsertDailyStep(request)
    }
}
