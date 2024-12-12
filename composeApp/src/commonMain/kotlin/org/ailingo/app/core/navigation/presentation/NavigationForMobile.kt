package org.ailingo.app.core.navigation.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import org.ailingo.app.ChatPage
import org.ailingo.app.DictionaryPage
import org.ailingo.app.ProfilePage
import org.ailingo.app.TopicsPage
import org.ailingo.app.core.navigation.model.BottomNavItem
import org.ailingo.app.core.presentation.topappbar.TopAppBarCenter
import org.ailingo.app.core.presentation.topappbar.TopAppBarWithProfile
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.features.login.presentation.LoginUiState
import org.ailingo.app.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationForMobile(
    navController: NavHostController,
    currentDestination: NavDestination?,
    isStandardCenterTopAppBarVisible: Boolean,
    isTopAppBarWithProfileVisible: Boolean,
    loginState: LoginUiState,
    windowInfo: WindowInfo,
    contentNavHost: @Composable (padding: PaddingValues) -> Unit,
) {
    val routesWithBottomBar = listOf(
        ChatPage::class,
        TopicsPage::class,
        DictionaryPage::class,
        ProfilePage::class,
    )

    val isBottomBarVisible = currentDestination?.let { dest ->
        routesWithBottomBar.any { routeClass ->
            dest.hasRoute(routeClass)
        }
    } ?: false

    val items = listOf(
        BottomNavItem.ChatMode,
        BottomNavItem.Topics,
        BottomNavItem.Dictionary,
        BottomNavItem.Profile,
    )

    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    LaunchedEffect(currentDestination) {
        currentDestination?.let { destination ->
            val matchingItemIndex = items.indexOfFirst { item ->
                when (item) {
                    BottomNavItem.ChatMode -> destination.hasRoute(ChatPage::class)
                    BottomNavItem.Topics -> destination.hasRoute(TopicsPage::class)
                    BottomNavItem.Dictionary -> destination.hasRoute(DictionaryPage::class)
                    BottomNavItem.Profile -> destination.hasRoute(ProfilePage::class)
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
            },
            bottomBar = {
                if (isBottomBarVisible) {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedItemIndex == index,
                                label = {
                                    Text(stringResource(item.title))
                                },
                                onClick = {
                                    navController.navigate(item.route) {
//                                        navController.graph.findStartDestination().route?.let {
//                                            popUpTo(it) {
//                                                saveState = true
//                                            }
//                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    selectedItemIndex = index
                                },
                                icon = {
                                    if (selectedItemIndex == index) {
                                        Icon(
                                            imageVector = item.selectedIcon,
                                            contentDescription = stringResource(item.title)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = item.unselectedIcon,
                                            contentDescription = stringResource(item.title)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            contentNavHost(innerPadding)
        }
    }
}