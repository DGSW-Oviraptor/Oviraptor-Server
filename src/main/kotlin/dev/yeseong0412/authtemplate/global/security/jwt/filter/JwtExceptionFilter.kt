package dev.yeseong0412.authtemplate.global.security.jwt.filter

import com.fasterxml.jackson.databind.ObjectMapper
import dev.yeseong0412.authtemplate.global.exception.CustomErrorCode
import dev.yeseong0412.authtemplate.global.exception.CustomException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtExceptionFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (exception: CustomException) {
            sendErrorResponse(response, exception)
        }
    }

    private fun sendErrorResponse(response: HttpServletResponse, exception: CustomException) {
        val error: CustomErrorCode = exception.customErrorCode

        response.status = error.status.value()
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val mapper = ObjectMapper()
        val map = HashMap<String, Any>()

        map["message"] = error.message
        map["status"] = error.status.value()

        response.writer.write(mapper.writeValueAsString(map))
    }
}