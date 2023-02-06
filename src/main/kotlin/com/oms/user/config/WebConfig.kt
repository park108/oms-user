package com.oms.user.config

import com.oms.user.filter.LoggingInterceptor
import com.oms.user.util.PrettyConverter
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(LoggingInterceptor(PrettyConverter()))
	}
}