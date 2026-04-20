package net.sanbook.walkly.walker.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.sanbook.walkly.walker.entity.Walker
import net.sanbook.walkly.walker.service.WalkerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "DB Test", description = "데이터베이스 연동 테스트")
@RestController
class DbTestController(
    private val walkerService: WalkerService
) {

    @Operation(summary = "워커 목록 조회", description = "walker 테이블의 전체 데이터를 조회합니다")
    @GetMapping("/api/db-test")
    fun getWalkers(): List<Walker> {
        return walkerService.getAllWalkers()
    }
}
