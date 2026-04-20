package net.sanbook.walkly.account.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.sanbook.walkly.account.data.AccountResponse
import net.sanbook.walkly.account.data.CreateAccountRequest
import net.sanbook.walkly.account.service.AccountService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Account", description = "계정/회원/인증")
@RestController
class AccountController(
    private val accountService: AccountService
) {
    @Operation(summary = "계정 생성", description = "계정을 생성합니다")
    @PostMapping("/account")
    fun createAccount(@RequestBody request: CreateAccountRequest): AccountResponse {
        return accountService.createAccount(request)
    }
}
