package org.ailingo.app.features.dictionary.main.presentation

import AiLingo.composeApp.BuildConfig.API_KEY_DICTIONARY
import AiLingo.composeApp.BuildConfig.BASE_URL
import AiLingo.composeApp.BuildConfig.BASE_URL_FREE_DICTIONARY
import AiLingo.composeApp.BuildConfig.BASE_URL_YANEX_DICTIONARY
import AiLingo.composeApp.BuildConfig.PREDICTOR_BASE_URL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.di.NetworkErrorMapper
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.dictionary.examples.data.model.WordInfoItem
import org.ailingo.app.features.dictionary.history.domain.DictionaryRepository
import org.ailingo.app.features.dictionary.history.domain.HistoryDictionaryUiState
import org.ailingo.app.features.dictionary.main.data.model.DictionaryResponse
import org.ailingo.app.features.dictionary.predictor.data.PredictorResponse

class DictionaryViewModel(
    private val historyDictionaryRepository: Deferred<DictionaryRepository>,
    private val httpClient: HttpClient,
    private val errorMapper: NetworkErrorMapper,
    private val tokenRepositoryDeferred: Deferred<TokenRepository>
) : ViewModel() {

    private val _historyOfDictionaryState = MutableStateFlow<HistoryDictionaryUiState>(HistoryDictionaryUiState.Loading)
    val historyOfDictionaryState = _historyOfDictionaryState.asStateFlow()

    private val _favoriteWords = MutableStateFlow<List<String>>(emptyList())
    val favoriteWords: StateFlow<List<String>> = _favoriteWords.asStateFlow()

    init {
        loadHistory()
        loadFavoriteWords()
    }

    private fun loadFavoriteWords() {
        viewModelScope.launch {
            try {
                val tokens = tokenRepositoryDeferred.await().getTokens()
                if (tokens == null) {
                    println("Not Authenticated")
                    return@launch
                }

                val response = httpClient.get("$BASE_URL/api/v1/words/favorites") {
                    header(HttpHeaders.Authorization, "Bearer ${tokens.token}")
                }
                _favoriteWords.update { response.body<List<String>>() }

            } catch (e: Exception) {
                val errorMessage = errorMapper.mapError(e)
                println("Error loading favorites: $errorMessage")
            }
        }
    }

    private fun addToFavourite(word: String) {
        viewModelScope.launch {
            try {
                val tokens = tokenRepositoryDeferred.await().getTokens()
                if (tokens == null) {
                    println("Not Authenticated")
                    return@launch
                }
                httpClient.post("$BASE_URL/api/v1/words/add")  {
                    header(HttpHeaders.Authorization, "Bearer ${tokens.token}")
                    parameter("word", word)
                }
                loadFavoriteWords()
            } catch (e: Exception) {
                val errorMessage = errorMapper.mapError(e)
                println("Error adding to favorites: $errorMessage")
            }
        }
    }

    private fun removeFromFavourite(word: String) {
        viewModelScope.launch {
            try {
                val tokens = tokenRepositoryDeferred.await().getTokens()
                if (tokens == null) {
                    println("Not Authenticated")
                    return@launch
                }

                httpClient.delete("$BASE_URL/api/v1/words/remove") {
                    header(HttpHeaders.Authorization, "Bearer ${tokens.token}")
                    parameter("word", word)
                }
                loadFavoriteWords()

            } catch (e: Exception) {
                val errorMessage = errorMapper.mapError(e)
                println("Error removing from favorites: $errorMessage")
            }
        }
    }

    private fun loadHistory() {
        _historyOfDictionaryState.value = HistoryDictionaryUiState.Loading
        viewModelScope.launch {
            historyDictionaryRepository.await().getDictionaryHistory().onEach { history ->
                _historyOfDictionaryState.value = HistoryDictionaryUiState.Success(history)
            }.launchIn(viewModelScope)
        }
    }

    private val _dictionaryUiState = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Empty)
    val dictionaryUiState: StateFlow<DictionaryUiState> = _dictionaryUiState.asStateFlow()

    private var _items = MutableStateFlow<PredictorResponse?>(null)
    val items: StateFlow<PredictorResponse?> = _items.asStateFlow()

    fun onEvent(event: DictionaryScreenEvents) {
        when (event) {
            is DictionaryScreenEvents.PredictNextWords -> {
                viewModelScope.launch {
                    val response = httpClient.post(PREDICTOR_BASE_URL) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        setBody(event.request)
                    }
                    _items.update {
                        response.body<PredictorResponse>()
                    }
                }
            }

            is DictionaryScreenEvents.SaveSearchedWord -> {
                viewModelScope.launch {
                    try {
                        historyDictionaryRepository.await().insertWordToHistory(event.word)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is DictionaryScreenEvents.SearchWordDefinition -> {
                viewModelScope.launch {
                    try {
                        _dictionaryUiState.value = DictionaryUiState.Loading

                        val deferredResponse = async {
                            try {
                                httpClient.get("$BASE_URL_YANEX_DICTIONARY?key=$API_KEY_DICTIONARY&lang=en-ru&text=${event.word}")
                                    .body<DictionaryResponse>()
                            } catch (e: Exception) {
                                DictionaryResponse(emptyList())
                            }
                        }

                        val deferredResponseExample = async {
                            try {
                                httpClient.get("$BASE_URL_FREE_DICTIONARY/${event.word}")
                                    .body<List<WordInfoItem>>()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                        val response = deferredResponse.await()
                        val responseExample = deferredResponseExample.await()

                        _dictionaryUiState.value = DictionaryUiState.Success(response, responseExample)
                    } catch (e: Exception) {
                        _dictionaryUiState.value =
                            DictionaryUiState.Error("Error occurred while fetching data. ${e.message}")
                    }
                }
            }

            is DictionaryScreenEvents.DeleteFromHistory -> {
                viewModelScope.launch {
                    try {
                        historyDictionaryRepository.await().deleteWordFromHistory(event.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is DictionaryScreenEvents.AddToFavorites -> {
                addToFavourite(event.word)
            }
            is DictionaryScreenEvents.RemoveFromFavorites -> {
                removeFromFavourite(event.word)
            }
        }
    }
}
