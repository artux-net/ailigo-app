package org.ailingo.app.core.navigation.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.ailingo.app.ChatPage
import org.ailingo.app.DictionaryPage
import org.ailingo.app.GetStartedPage
import org.ailingo.app.LoginPage
import org.ailingo.app.ProfilePage
import org.ailingo.app.RegisterPage
import org.ailingo.app.ResetPasswordPage
import org.ailingo.app.TopicsPage
import org.ailingo.app.UploadAvatarPage
import org.ailingo.app.core.utils.voice.VoiceToTextParser
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.features.chat.presentation.ChatScreen
import org.ailingo.app.features.dictionary.main.presentation.DictionaryScreen
import org.ailingo.app.features.dictionary.main.presentation.DictionaryViewModel
import org.ailingo.app.features.introduction.presentation.GetStartedScreen
import org.ailingo.app.features.login.presentation.LoginScreen
import org.ailingo.app.features.login.presentation.LoginScreenEvent
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.features.profile.presentation.ProfileScreen
import org.ailingo.app.features.registration.presentation.RegisterScreen
import org.ailingo.app.features.registration.presentation.RegisterViewModel
import org.ailingo.app.features.registration.presentation.uploadavatar.UploadAvatarScreen
import org.ailingo.app.features.registration.presentation.uploadavatar.UploadAvatarViewModel
import org.ailingo.app.features.resetpass.presentation.ResetPasswordScreen
import org.ailingo.app.features.topics.presentation.TopicsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    voiceToTextParser: VoiceToTextParser,
    registerViewModel: RegisterViewModel,
    innerPadding: PaddingValues,
    windowInfo: WindowInfo,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = LoginPage,
        modifier = modifier.padding(innerPadding)
    ) {
        composable<LoginPage> {
            LoginScreen(
                onNavigateToChatScreen = {
                    navController.navigate(ChatPage)
                },
                onNavigateToResetPasswordScreen = {
                    navController.navigate(ResetPasswordPage)
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(RegisterPage)
                },
                loginViewModel = loginViewModel
            )
        }
        composable<ChatPage> {
            ChatScreen(
                voiceToTextParser = voiceToTextParser,
                windowInfo = windowInfo,
                login = loginViewModel.login,
                password = loginViewModel.password
            )

        }
        composable<RegisterPage> {
            RegisterScreen(
                onNavigateToLoginScreen = {
                    navController.navigate(LoginPage)
                },
                onNavigateToUploadAvatarScreen = { login, password, email, name ->
                    navController.navigate(
                        UploadAvatarPage(
                            login,
                            password,
                            email,
                            name
                        )
                    )
                },
                registerViewModel = registerViewModel
            )
        }
        composable<ResetPasswordPage> {
            ResetPasswordScreen(
                onNavigateToSelectScreen = {
                    navController.navigate(GetStartedPage)
                }
            )
        }
        composable<GetStartedPage> {
            GetStartedScreen(
                onNavigateToLoginScreen = {
                    navController.navigate(LoginPage)
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(RegisterPage)
                }
            )
        }
        composable<UploadAvatarPage> { backStackEntry ->
            val args = backStackEntry.toRoute<UploadAvatarPage>()
            val uploadAvatarViewModel: UploadAvatarViewModel = koinViewModel<UploadAvatarViewModel>()
            UploadAvatarScreen(
                login = args.login,
                password = args.password,
                email = args.email,
                name = args.name,
                onNavigateToRegisterScreen = {
                    navController.navigate(RegisterPage)
                },
                onNavigateToChatScreen = {
                    loginViewModel.onEvent(
                        LoginScreenEvent.OnLoginUser(
                            args.login,
                            args.password
                        )
                    )
                    navController.navigate(ChatPage)
                },
                uploadAvatarViewModel = uploadAvatarViewModel
            )
        }
        composable<TopicsPage> {
            TopicsScreen(windowInfo = windowInfo)
        }
        composable<DictionaryPage> {
            val dictionaryViewModel: DictionaryViewModel = koinViewModel<DictionaryViewModel>()
            DictionaryScreen(dictionaryViewModel)
        }
        composable<ProfilePage> {
            ProfileScreen(
                loginUiState = loginViewModel.loginState.collectAsStateWithLifecycle().value,
                windowInfo = windowInfo,
                onExit = {
                    loginViewModel.onEvent(LoginScreenEvent.OnBackToEmptyState)
                    navController.navigate(LoginPage) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}