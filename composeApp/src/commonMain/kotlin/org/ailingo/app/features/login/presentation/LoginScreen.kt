package org.ailingo.app.features.login.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.back
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import org.ailingo.app.core.utils.presentation.ErrorScreen
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    onNavigateToChatScreen: () -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToResetPasswordScreen: () -> Unit,
    loginViewModel: LoginViewModel,
    loginState: LoginUiState
) {
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val isLoading = rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(isLoading.value) {
        if (isLoading.value) {
            delay(500L)
            isLoading.value = false
        }
    }

    when (loginState) {
        LoginUiState.Unauthenticated -> {
            LoginMainScreen(
                onLoginUser = {
                    loginViewModel.onEvent(
                        LoginScreenEvent.OnLoginUser(
                            loginViewModel.login,
                            loginViewModel.password
                        )
                    )
                },
                login = loginViewModel.login,
                onLoginChange = {
                    loginViewModel.login = it
                },
                password = loginViewModel.password,
                onPasswordChange = {
                    loginViewModel.password = it
                },
                passwordVisible,
                onPasswordVisibleChange = {
                    passwordVisible = !passwordVisible
                },
                isLoading,
                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                onNavigateToResetPasswordScreen = onNavigateToResetPasswordScreen
            )
        }

        is LoginUiState.Error -> {
            ErrorScreen(
                onButtonClick = {
                    loginViewModel.onEvent(LoginScreenEvent.OnBackToEmptyState)
                },
                buttonMessage = stringResource(Res.string.back),
                errorMessage = loginState.message
            )
        }

        LoginUiState.Loading -> {
            LoadingScreen()
        }

        is LoginUiState.Success -> {
            onNavigateToChatScreen()
        }
    }
}
