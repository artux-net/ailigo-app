package org.ailingo.app.features.registration.presentation.uploadavatar

import org.ailingo.app.features.registration.data.model.RegisterResponse

sealed class RegisterApiState {
    data class Success(
        val registerResponse: RegisterResponse
    ) : RegisterApiState()

    data class Error(val message: String) : RegisterApiState()
    data object Loading : RegisterApiState()
    data object Empty : RegisterApiState()
}
