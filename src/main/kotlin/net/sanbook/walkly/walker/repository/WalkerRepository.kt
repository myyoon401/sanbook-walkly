package net.sanbook.walkly.walker.repository

import net.sanbook.walkly.walker.entity.Walker
import org.springframework.data.jpa.repository.JpaRepository

interface WalkerRepository : JpaRepository<Walker, Long>
