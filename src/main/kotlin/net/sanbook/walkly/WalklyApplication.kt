package net.sanbook.walkly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WalklyApplication

fun main(args: Array<String>) {
	runApplication<WalklyApplication>(*args)
}
