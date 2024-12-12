package org.ailingo.app.features.login.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestBody(val login: String, val password: String)