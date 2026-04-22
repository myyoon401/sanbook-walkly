package net.sanbook.walkly.dailystep.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "daily_step",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["account_id", "date"])
    ]
)
class DailyStep(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val accountId: UUID,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false)
    var stepCount: Int,

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateStepCount(stepCount: Int) {
        this.stepCount = stepCount
    }
}
