package net.sanbook.walkly.dailystep.repository

import net.sanbook.walkly.dailystep.entity.DailyStep
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface DailyStepRepository : JpaRepository<DailyStep, Long> {
    fun findByAccountIdAndDate(accountId: UUID, date: LocalDate): DailyStep?
    fun findAllByAccountIdAndDateBetween(accountId: UUID, from: LocalDate, to: LocalDate): List<DailyStep>
}
