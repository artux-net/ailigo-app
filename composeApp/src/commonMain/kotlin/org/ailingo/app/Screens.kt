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
object SelectPage

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
