package net.sanbook.walkly.account.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "account")
class Account(
    @Id
    val id: UUID,

    @Column(nullable = false, unique = true)
    val loginId: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false, unique = true)
    val nickname: String,

    @Column(nullable = true)
    val profileImageUrl: String?,

    @Column(nullable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
