package com.oms.user.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

	override fun configure(http: HttpSecurity) {
		http.authorizeRequests()
			.antMatchers("/h2-console/**", "/h2", "/h2/**", "/", "/**", "/api", "/api/**")
			.access("permitAll")
			.and()
			.httpBasic()
			.and()
			.cors()
			.and()
			.csrf().disable()
			.headers().frameOptions().disable() // Set to use h2-console in browser
	}
}
