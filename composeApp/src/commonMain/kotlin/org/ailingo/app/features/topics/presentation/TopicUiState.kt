package org.ailingo.app.features.topics.presentation

import org.ailingo.app.features.topics.data.Topic

sealed class TopicUiState {
    data object Loading : TopicUiState()
    data class Success(val topics: List<Topic>) : TopicUiState()
    data class Error(val message: String) : TopicUiState()
    data object Empty: TopicUiState()
}