package net.sanbook.walkly.account.mapper

import net.sanbook.walkly.account.data.AccountResponse
import net.sanbook.walkly.account.data.CreateAccountRequest
import net.sanbook.walkly.account.entity.Account
import java.util.UUID

fun CreateAccountRequest.toEntity(): Account = Account(
    id = UUID.randomUUID(),
    loginId = loginId,
    password = password,
    email = email,
    nickname = nickname,
    profileImageUrl = profileImageUrl
)

fun Account.toResponse(): AccountResponse = AccountResponse(
    id = id,
    loginId = loginId,
    email = email,
    nickname = nickname,
    profileImageUrl = profileImageUrl
)
