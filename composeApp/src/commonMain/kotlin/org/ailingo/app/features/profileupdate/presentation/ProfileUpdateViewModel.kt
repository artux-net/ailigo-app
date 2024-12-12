package org.ailingo.app.features.profileupdate.presentation

import AiLingo.composeApp.BuildConfig.API_ENDPOINT_USER
import AiLingo.composeApp.BuildConfig.BASE_URL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.di.NetworkErrorMapper
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.profileupdate.data.UpdateProfileRequest
import org.ailingo.app.features.profileupdate.data.UpdateProfileResponse

class ProfileUpdateViewModel(
    private val httpClient: HttpClient,
    private val errorMapper: NetworkErrorMapper,
    private val tokenRepositoryDeferred: Deferred<TokenRepository>
) : ViewModel() {
    private val _profileUpdateUiState = MutableStateFlow<ProfileUpdateUiState>(ProfileUpdateUiState.Empty)
    val profileUpdateUiState = _profileUpdateUiState.asStateFlow()

    fun updateProfile(name: String, email: String, avatar: String) {
        viewModelScope.launch {
            _profileUpdateUiState.value = ProfileUpdateUiState.Loading
            try {
                val tokenRepository = tokenRepositoryDeferred.await()
                val tokens = tokenRepository.getTokens()
                if (tokens == null) {
                    _profileUpdateUiState.update { ProfileUpdateUiState.Error("Not authenticated") }
                    return@launch
                }
                val response = httpClient.post("$BASE_URL$API_ENDPOINT_USER/updateProfile") {
                    header(HttpHeaders.Authorization, "Bearer ${tokens.token}")
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(UpdateProfileRequest(name, email, avatar))
                }
                println(response)
                val responseData = response.body<UpdateProfileResponse>()
                if (responseData.success) {
                    _profileUpdateUiState.value = ProfileUpdateUiState.Success
                } else {
                    _profileUpdateUiState.value = ProfileUpdateUiState.Error(responseData.description)
                }
            } catch (e: Throwable) {
                _profileUpdateUiState.value = ProfileUpdateUiState.Error(errorMapper.mapError(e))
            }
        }
    }

    fun backToEmptyState() {
        _profileUpdateUiState.value = ProfileUpdateUiState.Empty
    }
}