package org.ailingo.app.features.login.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val login: String,
    val name: String,
    val email: String,
    val avatar: String,
    val xp: Int,
    val coins: Int,
    val streak: Int,
    val registration: String,
    val lastLoginAt: String
)