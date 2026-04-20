package net.sanbook.walkly.account.repository

import net.sanbook.walkly.account.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {
    fun existsByEmail(email: String): Boolean
}
