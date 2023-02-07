package com.oms.user.filter

import com.oms.user.util.PrettyConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggingInterceptor(
	val converter: PrettyConverter
) : HandlerInterceptor {

	companion object {
		private val logger: Logger = LoggerFactory.getLogger(this::class.java)
	}

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

		val wrapRequest = request as MultiAccessRequestWrapper

		if(null != request.contentType && "application/json" == request.contentType) {

			val bodyContents = converter.convert(wrapRequest.getContents())

			if ("null" == bodyContents) logger.info(
				"[REQUEST] {} {}", request.method, request.requestURL
			)
			else logger.info(
				"[REQUEST] {} {}\n######## BODY ########\n{}", request.method, request.requestURL, bodyContents
			)
		}

		return super.preHandle(request, response, handler)
	}

	override fun afterCompletion(
		request: HttpServletRequest
		, response: HttpServletResponse
		, handler: Any
		, ex: Exception?
	) {

		val wrapResponse = response as ContentCachingResponseWrapper
		val omsMessage = response.getHeader("oms-result-message")

		if(null != omsMessage) {

			val bodyContents = converter.convert(wrapResponse.contentAsByteArray)

			if ("null" == bodyContents) logger.info(
				"[RESPONSE] Status = {}, {}"
				, wrapResponse.status
				, omsMessage
			)
			else logger.info(
				"[RESPONSE] Status = {}, {}\n######## BODY ########\n{}"
				, wrapResponse.status
				, omsMessage
				, bodyContents
			)
		}

		super.afterCompletion(request, response, handler, ex)
	}
}