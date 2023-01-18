package com.oms.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}