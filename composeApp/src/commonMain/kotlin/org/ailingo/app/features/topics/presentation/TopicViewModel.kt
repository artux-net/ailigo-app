package org.ailingo.app.features.topics.presentation

import AiLingo.composeApp.BuildConfig.API_ENDPOINT_TOPICS
import AiLingo.composeApp.BuildConfig.BASE_URL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.di.NetworkErrorMapper
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.topics.data.Topic

class TopicViewModel(
    private val httpClient: HttpClient,
    private val errorMapper: NetworkErrorMapper,
    private val tokenRepositoryDeferred: Deferred<TokenRepository>
) : ViewModel() {
    private val _topicState = MutableStateFlow<TopicUiState>(TopicUiState.Empty)
    val topicState = _topicState.asStateFlow()

    init {
        getTopics()
    }

    private fun getTopics() {
        _topicState.value = TopicUiState.Loading
        viewModelScope.launch {
            try {
                val tokens = tokenRepositoryDeferred.await().getTokens()
                if (tokens == null) {
                    _topicState.update { TopicUiState.Error("Not authenticated") }
                    return@launch
                }

                val response = httpClient.get("$BASE_URL$API_ENDPOINT_TOPICS/getTopics") {
                    header(HttpHeaders.Authorization, "Bearer ${tokens.token}")
                    parameter("locale","ru")
                }

                println(response)
                println(tokens)


                _topicState.value = when {
                    response.status.isSuccess() -> {
                        val topics = response.body<List<Topic>>()
                        TopicUiState.Success(topics)
                    }
                    else -> {
                        TopicUiState.Error("Failed to fetch topics: ${response.status.value}")
                    }
                }
            } catch (e: Throwable) {
                _topicState.update { TopicUiState.Error(errorMapper.mapError(e)) }
            }
        }
    }
}