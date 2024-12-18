package org.ailingo.app.features.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SuccessRegister(
    val success: Boolean,
    val code: Int,
    val description: String,
    val failure: Boolean
)
