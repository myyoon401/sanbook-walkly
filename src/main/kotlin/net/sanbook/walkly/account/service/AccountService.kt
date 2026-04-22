package net.sanbook.walkly.account.service

import net.sanbook.walkly.account.data.AccountResponse
import net.sanbook.walkly.account.data.CreateAccountRequest
import net.sanbook.walkly.account.mapper.toEntity
import net.sanbook.walkly.account.mapper.toResponse
import net.sanbook.walkly.account.repository.AccountRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {
    fun createAccount(request: CreateAccountRequest): AccountResponse {
        if (accountRepository.existsByEmail(request.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다")
        }
        val savedAccount = accountRepository.save(request.toEntity())
        return savedAccount.toResponse()
    }

    fun validateAccountExists(accountId: UUID) {
        if (!accountRepository.existsById(accountId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.")
    }
}
