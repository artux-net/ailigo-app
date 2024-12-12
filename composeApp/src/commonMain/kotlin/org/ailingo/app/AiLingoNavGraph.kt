package org.ailingo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.ailingo.app.core.navigation.presentation.AppNavHost
import org.ailingo.app.core.navigation.presentation.NavigationForDesktop
import org.ailingo.app.core.navigation.presentation.NavigationForMobile
import org.ailingo.app.core.utils.voice.VoiceToTextParser
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.core.utils.windowinfo.info.rememberWindowInfo
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.features.registration.presentation.RegisterViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AiLingoNavGraph(
    modifier: Modifier = Modifier,
    voiceToTextParser: VoiceToTextParser,
    navController: NavHostController = rememberNavController()
) {
    val loginViewModel: LoginViewModel = koinViewModel<LoginViewModel>()
    val loginState = loginViewModel.loginState.collectAsStateWithLifecycle().value
    val registerViewModel: RegisterViewModel = koinViewModel<RegisterViewModel>()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val routesProfileVisible = listOf(
        ChatPage::class,
        DictionaryPage::class,
        TopicsPage::class,
        ProfilePage::class,
        ProfileUpdatePage::class,
    )

    val isTopAppBarWithProfileVisible = currentDestination?.let { dest ->
        routesProfileVisible.any { routeClass ->
            dest.hasRoute(routeClass)
        }
    } ?: false

    val routesWithStandardTopAppBar = listOf(
        LoginPage::class,
        RegisterPage::class,
        UploadAvatarPage::class,
        GetStartedPage::class,
        ResetPasswordPage::class
    )

    val isStandardCenterTopAppBarVisible = currentDestination?.let { dest ->
        routesWithStandardTopAppBar.any { routeClass ->
            dest.hasRoute(routeClass)
        }
    } ?: false

    val windowInfo = rememberWindowInfo()
    if (windowInfo.screenWidthInfo is WindowInfo.WindowType.DesktopWindowInfo) {
        NavigationForDesktop(
            navController = navController,
            isStandardCenterTopAppBarVisible = isStandardCenterTopAppBarVisible,
            isTopAppBarWithProfileVisible = isTopAppBarWithProfileVisible,
            loginViewModel = loginViewModel,
            currentDestination = currentDestination,
            windowInfo = windowInfo,
            loginState = loginState
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                loginViewModel = loginViewModel,
                loginState = loginState,
                voiceToTextParser = voiceToTextParser,
                registerViewModel = registerViewModel,
                innerPadding = innerPadding,
                windowInfo = windowInfo
            )
        }
    } else {
        NavigationForMobile(
            navController = navController,
            currentDestination = currentDestination,
            isStandardCenterTopAppBarVisible = isStandardCenterTopAppBarVisible,
            isTopAppBarWithProfileVisible = isTopAppBarWithProfileVisible,
            loginState = loginState,
            windowInfo = windowInfo
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                loginViewModel = loginViewModel,
                loginState = loginState,
                voiceToTextParser = voiceToTextParser,
                registerViewModel = registerViewModel,
                innerPadding = innerPadding,
                windowInfo = windowInfo
            )
        }
    }
}