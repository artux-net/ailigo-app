package org.ailingo.app.features.profileupdate.data

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val avatar: String
)