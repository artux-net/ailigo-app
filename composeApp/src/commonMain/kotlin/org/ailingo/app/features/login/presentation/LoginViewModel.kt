package org.ailingo.app.features.login.presentation

import AiLingo.composeApp.BuildConfig.API_ENDPOINT_USER
import AiLingo.composeApp.BuildConfig.BASE_URL
import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.wrong_login_or_password
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.di.NetworkErrorMapper
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.login.data.LoginRequestBody
import org.ailingo.app.features.login.data.LoginResponse
import org.ailingo.app.features.login.data.User
import org.jetbrains.compose.resources.getString

class LoginViewModel(
    private val httpClient: HttpClient,
    private val errorMapper: NetworkErrorMapper,
    private val tokenRepositoryDeferred: Deferred<TokenRepository>
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    var login by mutableStateOf("")
    var password by mutableStateOf("")

    init {
        checkUserAuth()
    }

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            LoginScreenEvent.OnBackToEmptyState -> {
                _loginState.value = LoginUiState.Unauthenticated
                viewModelScope.launch {
                    val tokenRepository = tokenRepositoryDeferred.await()
                    tokenRepository.deleteTokens()
                }
            }

            is LoginScreenEvent.OnLoginUser -> {
                loginUser(event.login, event.password)
            }
        }
    }

     fun checkUserAuth() {
        viewModelScope.launch {
            val tokens = tokenRepositoryDeferred.await().getTokens()
            if (tokens != null) {
                val infoResponse = try {
                    httpClient.get("$BASE_URL$API_ENDPOINT_USER/info") {
                        header(HttpHeaders.Authorization, "Bearer ${tokens.token}")
                    }
                } catch (e: Throwable) {
                    _loginState.value = LoginUiState.Unauthenticated
                    return@launch
                }

                if (infoResponse.status.isSuccess()) {
                    val userInfo = infoResponse.body<User>()
                    _loginState.value = LoginUiState.Success(
                        userInfo,
                        token = tokens.token,
                        refreshToken = tokens.refreshToken
                    )
                } else {
                    _loginState.value = LoginUiState.Unauthenticated
                }
            } else {
                _loginState.value = LoginUiState.Unauthenticated
            }
        }
    }

    private fun loginUser(
        login: String,
        password: String
    ) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val response = httpClient.post("$BASE_URL$API_ENDPOINT_USER/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(LoginRequestBody(login, password))
                }
                println(response)
                _loginState.value = when {
                    response.status.isSuccess() -> {
                        val loginResponse = response.body<LoginResponse>()
                        tokenRepositoryDeferred.await().saveTokens(loginResponse)
                        LoginUiState.Success(
                            user = loginResponse.user,
                            token = loginResponse.token,
                            refreshToken = loginResponse.refreshToken
                        )
                    }

                    else -> {
                        if (response.status.value == 401) {
                            LoginUiState.Error(getString(Res.string.wrong_login_or_password))
                        } else {
                            println(response)
                            println(response.status)
                            println(response.status.value)
                            println(response.status.description)
                            println(response.body<Any>())
                            println(response.request)
                            println(response.call)
                            println(response.bodyAsText())
                            LoginUiState.Error(response.status.description)
                        }
                    }
                }
                println(_loginState.value)
            } catch (e: Throwable) {
                _loginState.update { LoginUiState.Error(errorMapper.mapError(e)) }
            }
        }
    }
}