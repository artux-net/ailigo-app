package org.ailingo.app.features.profileupdate.presentation

sealed class ProfileUpdateUiState {
    data object Empty: ProfileUpdateUiState()
    data object Loading: ProfileUpdateUiState()
    data object Success: ProfileUpdateUiState()
    data class Error(val message: String) : ProfileUpdateUiState()
}