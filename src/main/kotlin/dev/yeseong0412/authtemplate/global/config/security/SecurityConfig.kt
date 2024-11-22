package dev.yeseong0412.authtemplate.global.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import dev.yeseong0412.authtemplate.global.security.jwt.filter.JwtAuthenticationFilter
import dev.yeseong0412.authtemplate.global.security.jwt.filter.JwtExceptionFilter
import dev.yeseong0412.authtemplate.global.security.jwt.handler.JwtAccessDeniedHandler
import dev.yeseong0412.authtemplate.global.security.jwt.handler.JwtAuthenticationEntryPoint
import dev.yeseong0412.authtemplate.global.security.jwt.util.JwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val jwtUtils: JwtUtils,
    private val objectMapper: ObjectMapper,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
    private val jwtExceptionFilter: JwtExceptionFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors {
                corsConfigurationSource()
            }

            .csrf {
                it.disable()
            }

            .formLogin {
                it.disable()
            }

            .sessionManagement { session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }

            .authorizeHttpRequests {
                it
                    .requestMatchers( "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/ws-info/**").permitAll()
                    .requestMatchers("/ws/**","/stomp/chat/**").permitAll()
                    .anyRequest().authenticated()
            }

            .exceptionHandling {
                it.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                it.accessDeniedHandler(jwtAccessDeniedHandler)
            }

            .addFilterBefore(JwtAuthenticationFilter(jwtUtils, objectMapper), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.addAllowedOriginPattern("*")
        corsConfiguration.addAllowedHeader("Content-Type")
        corsConfiguration.addAllowedHeader("Authorization")
        corsConfiguration.addAllowedHeader("Accept")
        corsConfiguration.addAllowedMethod("GET")
        corsConfiguration.addAllowedMethod("POST")
        corsConfiguration.addAllowedMethod("PATCH")
        corsConfiguration.addAllowedMethod("DELETE")
        corsConfiguration.allowCredentials = true

        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration)

        return urlBasedCorsConfigurationSource
    }
}