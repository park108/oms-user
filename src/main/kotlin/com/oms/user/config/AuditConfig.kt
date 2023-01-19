package com.oms.user.config

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

private val logger = KotlinLogging.logger {}

@Configuration
class AuditConfig : AuditorAware<String> {

    @Override
    override fun getCurrentAuditor(): Optional<String> {

        val req = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val userId = req.request.getHeader("userId")

        logger.info("## User id from request header = $userId")

        return when(userId) {
            null -> Optional.of("anonymous")
            else -> Optional.of(userId)
        }
    }
}