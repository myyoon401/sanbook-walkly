package net.sanbook.walkly.account.data

import java.util.UUID

data class AccountResponse(
    val id: UUID,
    val loginId: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?
)
