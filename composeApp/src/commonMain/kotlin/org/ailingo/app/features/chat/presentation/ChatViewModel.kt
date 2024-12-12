package org.ailingo.app.features.chat.presentation

import AiLingo.composeApp.BuildConfig.BASE_URL
import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.bot_greeting_topic1
import ailingo.composeapp.generated.resources.bot_greeting_topic2
import ailingo.composeapp.generated.resources.bot_greeting_topic3
import ailingo.composeapp.generated.resources.bot_greeting_topic4
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.ailingo.app.di.NetworkErrorMapper
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.chat.data.model.Message
import org.jetbrains.compose.resources.getString
import kotlin.random.Random

class ChatViewModel(
    private val httpClient: HttpClient,
    private val errorMapper: NetworkErrorMapper,
    private val tokenRepositoryDeferred: Deferred<TokenRepository>
) : ViewModel() {
    private val _chatState = mutableStateListOf<Message>()
    val chatState: List<Message> = _chatState

    private val _isActiveJob = MutableSharedFlow<Boolean>()
    val isActiveJob = _isActiveJob.asSharedFlow()

    init {
        viewModelScope.launch {
            val randomGreeting = when (Random.nextInt(4)) {
                0 -> getString(Res.string.bot_greeting_topic1)
                1 -> getString(Res.string.bot_greeting_topic2)
                2 -> getString(Res.string.bot_greeting_topic3)
                else -> getString(Res.string.bot_greeting_topic4)
            }
            _chatState.add(Message(randomGreeting, false))
        }
    }

    private val API_ENDPOINT = "/api/v1/chat/message"

    fun onEvent(event: ChatScreenEvents) {
        when (event) {
            is ChatScreenEvents.MessageSent -> {
                _chatState.add(Message(event.message, isSentByUser = true))
                _chatState.add(Message("Waiting for response...", isSentByUser = false))
                viewModelScope.launch {
                    try {
                        val token = tokenRepositoryDeferred.await().getTokens()?.token
                        val response = httpClient.post("$BASE_URL$API_ENDPOINT") {
                            header(HttpHeaders.Authorization, "Bearer $token")
                            header(HttpHeaders.ContentType, ContentType.Application.Json)
                            setBody(event.message)
                        }
                        if (token == null) {
                            _chatState.removeAt(_chatState.size - 1)
                            _chatState.add(Message("Please login", isSentByUser = false))
                            _isActiveJob.emit(false)
                            return@launch
                        }
                        when {
                            response.status.isSuccess() -> {
                                val responseBody = response.body<String>()
                                _chatState.removeAt(_chatState.size - 1)
                                _chatState.add(Message(responseBody, isSentByUser = false))
                                _isActiveJob.emit(false)
                            } else -> {
                                _chatState.removeAt(_chatState.size - 1)
                                _chatState.add(
                                    Message(
                                        "Request failed with ${response.status}",
                                        isSentByUser = false
                                    )
                                )
                                _isActiveJob.emit(false)
                            }
                        }
                    } catch (e: Exception) {
                        _chatState.removeAt(_chatState.size - 1)
                        _chatState.add(Message(errorMapper.mapError(e), isSentByUser = false))
                        _isActiveJob.emit(false)
                    }
                }
            }
        }
    }
}