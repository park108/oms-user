package com.oms.user.filter

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ServletWrappingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest
        , response: HttpServletResponse
        , filterChain: FilterChain) {

        val wrapRequest = MultiAccessRequestWrapper(request)
        val wrapResponse = ContentCachingResponseWrapper(response)

        filterChain.doFilter(wrapRequest, wrapResponse)
        wrapResponse.copyBodyToResponse()
    }
}