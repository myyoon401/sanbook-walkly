package net.sanbook.walkly.common.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Health", description = "서버 상태 확인")
@RestController
class HealthController {

    @Operation(summary = "헬스체크", description = "서버 상태와 현재 타임스탬프를 반환합니다")
    @GetMapping("/api/health")
    fun healthCheck(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now()
        )
    }
}
