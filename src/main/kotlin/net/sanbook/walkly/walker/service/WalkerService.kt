package net.sanbook.walkly.walker.service

import net.sanbook.walkly.walker.entity.Walker
import net.sanbook.walkly.walker.repository.WalkerRepository
import org.springframework.stereotype.Service

@Service
class WalkerService(
    private val walkerRepository: WalkerRepository
) {

    fun getAllWalkers(): List<Walker> {
        return walkerRepository.findAll()
    }
}
