package org.ailingo.app

import kotlinx.serialization.Serializable

@Serializable
object LandingPage

@Serializable
object LoginPage

@Serializable
object ChatPage

@Serializable
object RegisterPage

@Serializable
object ResetPasswordPage

@Serializable
object GetStartedPage

@Serializable
data class UploadAvatarPage(
    val login: String,
    val password: String,
    val email: String,
    val name: String
)

@Serializable
object TopicsPage

@Serializable
object DictionaryPage

@Serializable
object ProfilePage

@Serializable
data class ProfileUpdatePage(
    val name: String,
    val email: String,
    var avatar: String
)
