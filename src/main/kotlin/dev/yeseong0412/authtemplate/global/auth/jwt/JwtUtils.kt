package dev.yeseong0412.authtemplate.global.auth.jwt

import dev.yeseong0412.authtemplate.domain.user.domain.model.User
import dev.yeseong0412.authtemplate.global.auth.jwt.exception.type.JwtErrorType
import io.jsonwebtoken.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtils(
    private val jwtProperties: JwtProperties,
    private val userDetailsService: UserDetailsService,
) {

    private val secretKey: SecretKey = SecretKeySpec(
        this.jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8),
        SignatureAlgorithm.HS256.jcaName
    )

    fun getUsername(token: String): String {
        val claims = parseClaims(token)
        return claims["email"] as String
    }

    fun checkTokenInfo(token: String): JwtErrorType {
        return try {
            parseClaims(token) // 유효성 검증
            JwtErrorType.OK
        } catch (e: ExpiredJwtException) {
            JwtErrorType.ExpiredJwtException
        } catch (e: SignatureException) {
            JwtErrorType.SignatureException
        } catch (e: MalformedJwtException) {
            JwtErrorType.MalformedJwtException
        } catch (e: UnsupportedJwtException) {
            JwtErrorType.UnsupportedJwtException
        } catch (e: IllegalArgumentException) {
            JwtErrorType.IllegalArgumentException
        } catch (e: Exception) {
            JwtErrorType.UNKNOWN_EXCEPTION
        }
    }

    fun getToken(token: String): String {
        return token.removePrefix("Bearer ")
    }

    fun generate(user: User): JwtInfo {
        val accessToken = createToken(
            user = user,
            tokenExpired = jwtProperties.accessExpired
        )
        val refreshToken = createToken(
            user = user,
            tokenExpired = jwtProperties.refreshExpired
        )

        return JwtInfo("Bearer $accessToken", "Bearer $refreshToken")
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(getUsername(getToken(token)))
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    fun refreshToken(user: User): String {
        return "Bearer " + createToken(
            user = user,
            tokenExpired = jwtProperties.accessExpired
        )
    }

    private fun createToken(user: User, tokenExpired: Long): String {
        val now: Long = Date().time
        return Jwts.builder()
            .claim("id", user.id)
            .claim("email", user.email)
            .claim("role", user.role)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + tokenExpired))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
    }
}
