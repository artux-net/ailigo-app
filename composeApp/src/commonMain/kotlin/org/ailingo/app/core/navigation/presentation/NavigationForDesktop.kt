package org.ailingo.app.core.navigation.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import org.ailingo.app.ChatPage
import org.ailingo.app.DictionaryPage
import org.ailingo.app.LoginPage
import org.ailingo.app.ProfilePage
import org.ailingo.app.ProfileUpdatePage
import org.ailingo.app.TopicsPage
import org.ailingo.app.core.presentation.model.DrawerItems
import org.ailingo.app.core.presentation.topappbar.TopAppBarCenter
import org.ailingo.app.core.presentation.topappbar.TopAppBarWithProfile
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.features.login.presentation.LoginScreenEvent
import org.ailingo.app.features.login.presentation.LoginUiState
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationForDesktop(
    navController: NavHostController,
    isStandardCenterTopAppBarVisible: Boolean,
    isTopAppBarWithProfileVisible: Boolean,
    loginViewModel: LoginViewModel,
    loginState: LoginUiState,
    currentDestination: NavDestination?,
    windowInfo: WindowInfo,
    contentNavHost: @Composable (padding: PaddingValues) -> Unit
) {
    val routesWithNavigationDrawer = listOf(
        ChatPage::class,
        TopicsPage::class,
        DictionaryPage::class,
        ProfilePage::class,
        ProfileUpdatePage::class,
    )

    val isNavigationDrawerVisible = currentDestination?.let { dest ->
        routesWithNavigationDrawer.any { routeClass ->
            dest.hasRoute(routeClass)
        }
    } ?: false

    val items = listOf(
        DrawerItems.ChatMode,
        DrawerItems.Topics,
        DrawerItems.Dictionary,
        DrawerItems.Profile,
        DrawerItems.Exit,
    )

    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(currentDestination) {
        currentDestination?.let { destination ->
            val matchingItemIndex = items.indexOfFirst { item ->
                when (item) {
                    DrawerItems.ChatMode -> destination.hasRoute(ChatPage::class)
                    DrawerItems.Topics -> destination.hasRoute(TopicsPage::class)
                    DrawerItems.Dictionary -> destination.hasRoute(DictionaryPage::class)
                    DrawerItems.Profile -> destination.hasRoute(ProfilePage::class)
                    else -> false
                }
            }
            if (matchingItemIndex != -1 && matchingItemIndex != selectedItemIndex) {
                selectedItemIndex = matchingItemIndex
            }
        }
    }

    AppTheme {
        Scaffold(
            topBar = {
                if (isTopAppBarWithProfileVisible) {
                    TopAppBarWithProfile(loginState = loginState, windowInfo = windowInfo)
                } else {
                    if (isStandardCenterTopAppBarVisible) {
                        TopAppBarCenter()
                    }
                }
            }
        ) { innerPadding ->
            PermanentNavigationDrawer(
                modifier = Modifier,
                drawerContent = {
                    if (isNavigationDrawerVisible) {
                        PermanentDrawerSheet(
                            modifier = Modifier.padding(top = 64.dp).width(350.dp).fillMaxHeight(),
                            drawerContainerColor = Color.White
                        ) {
                            items.forEachIndexed { index, item ->
                                if (item == DrawerItems.Exit) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                NavigationDrawerItem(
                                    icon = { Icon(item.icon, contentDescription = null) },
                                    label = { Text(stringResource(item.title)) },
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        when (item) {
                                            DrawerItems.ChatMode -> navController.navigate(ChatPage)
                                            DrawerItems.Topics -> navController.navigate(TopicsPage)
                                            DrawerItems.Dictionary -> navController.navigate(
                                                DictionaryPage
                                            )

                                            DrawerItems.Profile -> navController.navigate(
                                                ProfilePage
                                            )

                                            DrawerItems.Exit -> {
                                                navController.navigate(LoginPage)
                                                loginViewModel.onEvent(LoginScreenEvent.OnBackToEmptyState)
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                )
                            }
                        }
                    }
                }
            ) {
                contentNavHost(innerPadding)
            }
        }
    }
}