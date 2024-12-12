package org.ailingo.app.features.profileupdate.data

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileResponse(
    val success: Boolean,
    val code: Int,
    val description: String,
    val failure: Boolean
)