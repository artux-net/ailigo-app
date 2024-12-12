package org.ailingo.app.features.registration.data.model

import kotlinx.serialization.Serializable
import org.ailingo.app.features.login.data.User

@Serializable
data class RegisterResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)