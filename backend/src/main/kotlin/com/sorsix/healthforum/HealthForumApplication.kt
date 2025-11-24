package com.sorsix.healthforum

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HealthForumApplication

fun main(args: Array<String>) {
    runApplication<HealthForumApplication>(*args)
}
