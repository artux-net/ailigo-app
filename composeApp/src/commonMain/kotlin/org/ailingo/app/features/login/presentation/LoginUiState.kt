package org.ailingo.app.features.login.presentation

import kotlinx.serialization.Serializable
import org.ailingo.app.features.login.data.User

@Serializable
sealed class LoginUiState {
    @Serializable
    data class Success(
        val user: User,
        val token: String,
        val refreshToken: String
    ) : LoginUiState()

    data class Error(val message: String) : LoginUiState()
    data object Loading : LoginUiState()
    data object Unauthenticated : LoginUiState()
}