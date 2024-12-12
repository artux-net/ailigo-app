package org.ailingo.app.features.auth.domain

import org.ailingo.app.features.auth.data.model.Token
import org.ailingo.app.features.login.data.LoginResponse

interface TokenRepository {
    suspend fun saveTokens(loginResponse: LoginResponse)
    suspend fun getTokens(): Token?
    suspend fun updateTokens(newToken: String, newRefreshToken: String, oldRefreshToken: String)
    suspend fun deleteToken(token: String)
    suspend fun deleteRefreshToken(refreshToken: String)
    suspend fun deleteTokens()
}