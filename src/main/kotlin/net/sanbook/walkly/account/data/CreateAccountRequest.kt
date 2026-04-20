package net.sanbook.walkly.account.data

data class CreateAccountRequest(
    val loginId: String,
    val password: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?
)
