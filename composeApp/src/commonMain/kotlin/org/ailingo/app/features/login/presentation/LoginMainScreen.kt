package org.ailingo.app.features.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.utils.presentation.LoadingScreen

@Composable
fun LoginMainScreen(
    onLoginUser: () -> Unit,
    login: String,
    onLoginChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: () -> Unit,
    isLoading: MutableState<Boolean>,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToResetPasswordScreen: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val passwordFieldFocusRequester = rememberUpdatedState(FocusRequester())
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoginEmpty = remember {
        mutableStateOf(false)
    }
    val isPasswordEmpty = remember {
        mutableStateOf(false)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        if (isLoading.value) {
            LoadingScreen()
        } else {
            LoginWelcomeMessage()
            Spacer(modifier = Modifier.height(8.dp))
            LoginTextField(
                textValue = login,
                onValueChange = {
                    onLoginChange(it)
                },
                onNext = {
                    passwordFieldFocusRequester.value.requestFocus()
                },
                showErrorText = isLoginEmpty.value,
            )

            LoginPasswordTextField(
                value = password,
                onValueChange = {
                    onPasswordChange(it)
                },
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = onPasswordVisibleChange,
                focusRequester = passwordFieldFocusRequester.value,
                focusManager = focusManager,
                keyboardController = keyboardController,
                onLoginUser = onLoginUser,
                showErrorText = isPasswordEmpty.value
            )
            Spacer(modifier = Modifier.height(12.dp))
            LoginForgotPasswordLink(onClick = {
                onNavigateToResetPasswordScreen()
            })
            Spacer(modifier = Modifier.height(16.dp))
            LoginButton(
                onClick = onLoginUser,
                login,
                password,
                isLoginEmpty,
                isPasswordEmpty,
                isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))
            LoginSignUpSection(onClick = {
                onNavigateToRegisterScreen()
            })
        }
    }
}
