package org.ailingo.app.features.registration.presentation.uploadavatar

import AiLingo.composeApp.BuildConfig.API_ENDPOINT_USER
import AiLingo.composeApp.BuildConfig.BASE_URL
import AiLingo.composeApp.BuildConfig.BASE_URL_UPLOAD_IMAGE
import AiLingo.composeApp.BuildConfig.UPLOAD_IMAGE_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.ailingo.app.di.NetworkErrorMapper
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.login.data.LoginResponse
import org.ailingo.app.features.registration.data.image.UploadImageResponse
import org.ailingo.app.features.registration.data.model.RegisterResponse
import org.ailingo.app.features.registration.data.model.UserRegistrationData

class UploadAvatarViewModel (
    private val httpClient: HttpClient,
    private val errorMapper: NetworkErrorMapper,
    private val tokenRepositoryDeferred: Deferred<TokenRepository>
): ViewModel() {
    fun onEvent(event: UploadAvatarEvent) {
        when (event) {
            UploadAvatarEvent.OnBackToEmptyRegisterState -> {
                _registerState.update { RegisterApiState.Empty }
            }

            UploadAvatarEvent.OnBackToEmptyUploadAvatar -> {
                _imageState.update { UploadImageUiState.EmptyImage }
            }

            is UploadAvatarEvent.OnUploadImage -> {
                uploadImage(event.image)
            }

            is UploadAvatarEvent.RegisterUser -> {
                registerUser(event.user)
            }
        }
    }

    private val _registerState = MutableStateFlow<RegisterApiState>(RegisterApiState.Empty)
    val registerState: StateFlow<RegisterApiState> = _registerState.asStateFlow()

    fun registerUser(user: UserRegistrationData) {
        viewModelScope.launch {
            _registerState.value = RegisterApiState.Loading
            try {
                val response = httpClient.post("$BASE_URL$API_ENDPOINT_USER/register") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(user)
                }

                if (response.status.isSuccess()) {
                    val registerResponse = response.body<RegisterResponse>()
                    tokenRepositoryDeferred.await().saveTokens(
                        loginResponse = LoginResponse(
                            token = registerResponse.token,
                            refreshToken = registerResponse.refreshToken,
                            user = registerResponse.user
                        ),
                    )
                    _registerState.value = RegisterApiState.Success(registerResponse)
                } else {
                    _registerState.value = RegisterApiState.Error(response.status.value.toString())
                }

            } catch (e: Throwable) {
                _registerState.value = RegisterApiState.Error(errorMapper.mapError(e))
            }
        }
    }

    private val _imageState = MutableStateFlow<UploadImageUiState>(UploadImageUiState.EmptyImage)
    val imageState = _imageState.asStateFlow()

    @OptIn(InternalAPI::class, InternalAPI::class)
    fun uploadImage(base64Image: String) {
        _imageState.value = UploadImageUiState.LoadingImage
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = Logger.DEFAULT
            }
        }
        viewModelScope.launch {
            try {
                val response = httpClient.post("$BASE_URL_UPLOAD_IMAGE?key=$UPLOAD_IMAGE_KEY") {
                    body = MultiPartFormDataContent(
                        formData {
                            append("image", base64Image)
                        }
                    )
                }
                if (response.status.isSuccess()) {
                    val body = response.body<UploadImageResponse>()
                    _imageState.value = UploadImageUiState.Success(uploadImageResponse = body)
                } else {
                    _imageState.value = UploadImageUiState.Error("error: ${response.status}")
                }
            } catch (e: Exception) {
                _imageState.value = UploadImageUiState.Error("error: ${e.message}")
            } finally {
                httpClient.close()
            }
        }
    }
}
