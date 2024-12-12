package org.ailingo.app.features.profile.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.change_user_data
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.exit
import ailingo.composeapp.generated.resources.icon_experience
import ailingo.composeapp.generated.resources.profile_background
import ailingo.composeapp.generated.resources.streak
import ailingo.composeapp.generated.resources.xp
import ailingo.composeapp.generated.resources.your_statistics
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import org.ailingo.app.core.utils.presentation.ErrorScreen
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.features.login.presentation.LoginUiState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    loginUiState: LoginUiState,
    windowInfo: WindowInfo,
    onExit: () -> Unit,
    onNavigateProfileChange: (
        name: String,
        email: String,
        avatar: String
    ) -> Unit
) {
    when (loginUiState) {
        is LoginUiState.Error -> {
            ErrorScreen(errorMessage = loginUiState.message)
        }

        LoginUiState.Loading -> {
            LoadingScreen()
        }

        is LoginUiState.Success -> {
            ProfileContent(
                modifier = modifier,
                loginUiState = loginUiState,
                onExit = onExit,
                windowInfo = windowInfo,
                onNavigateProfileChange = {
                    onNavigateProfileChange(
                        loginUiState.user.name,
                        loginUiState.user.email,
                        loginUiState.user.avatar
                    )
                }
            )
        }

        LoginUiState.Unauthenticated -> {}
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier,
    windowInfo: WindowInfo,
    loginUiState: LoginUiState.Success,
    onExit: () -> Unit,
    onNavigateProfileChange: () -> Unit
) {
    val statisticCardWidth = remember {
        mutableStateOf(0.dp)
    }
    val statisticCardHeight = remember {
        mutableStateOf(0.dp)
    }

    val density = LocalDensity.current

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileHeader(loginUiState)
        if (windowInfo.screenWidthInfo == WindowInfo.WindowType.DesktopWindowInfo) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        ProfileData(
                            loginUiState = loginUiState,
                            statisticsHeight = statisticCardHeight.value,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        ProfileStats(
                            loginUiState,
                            cardWidth = statisticCardWidth,
                            cardHeight = statisticCardHeight,
                            density = density,
                        )
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxWidth(),
            ) {
                ProfileData(
                    loginUiState = loginUiState,
                    statisticsHeight = statisticCardHeight.value,
                )
                ProfileStats(
                    loginUiState,
                    cardWidth = statisticCardWidth,
                    cardHeight = statisticCardHeight,
                    density = density,
                )
            }
        }
        ProfileChangeDataButton(statisticCardWidth.value, onNavigateProfileChange = onNavigateProfileChange)
        ProfileExitButton(onExit, statisticCardWidth.value)
    }
}


@Composable
fun ProfileHeader(loginUiState: LoginUiState.Success) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(Res.drawable.profile_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(200.dp),
        )
        if (loginUiState.user.avatar.isBlank()) {
            Card(
                modifier = Modifier.padding(top = 100.dp).align(Alignment.Center).size(200.dp),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(Res.drawable.defaultProfilePhoto),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
            Card(
                modifier = Modifier.padding(top = 100.dp).align(Alignment.Center).size(200.dp),
                shape = CircleShape
            ) {
                SubcomposeAsyncImage(
                    model = loginUiState.user.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                ) {
                    val state by painter.state.collectAsState()
                    when (state) {
                        AsyncImagePainter.State.Empty -> {}
                        is AsyncImagePainter.State.Error -> {
                            Image(
                                painter = painterResource(Res.drawable.defaultProfilePhoto),
                                contentDescription = null
                            )
                        }

                        is AsyncImagePainter.State.Loading -> {
                            LoadingScreen()
                        }

                        is AsyncImagePainter.State.Success -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileData(
    modifier: Modifier = Modifier,
    loginUiState: LoginUiState.Success,
    statisticsHeight: Dp
) {
    Card(
        colors = CardDefaults
            .cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp).height(statisticsHeight)
        ) {
            Text(
                loginUiState.user.name,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                loginUiState.user.login,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                loginUiState.user.email,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun ProfileStats(
    loginUiState: LoginUiState.Success,
    cardWidth: MutableState<Dp>,
    cardHeight: MutableState<Dp>,
    density: Density,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier.onGloballyPositioned { coordinates ->
            cardWidth.value = with(density) { coordinates.size.width.toDp() }
            cardHeight.value = with(density) { coordinates.size.height.toDp() }
        }) {
        Text(
            text = stringResource(Res.string.your_statistics),
            modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(4.dp),
        ) {
            StatRow(loginUiState)
        }
    }
}

@Composable
fun StatRow(loginUiState: LoginUiState.Success) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        StatItem(
            value = loginUiState.user.coins.toString(),
            label = stringResource(Res.string.coins),
            icon = Res.drawable.coins
        )
        HorizontalDivider(modifier = Modifier.height(32.dp).width(2.dp))
        StatItem(
            value = loginUiState.user.streak.toString(),
            label = stringResource(Res.string.streak),
            icon = Res.drawable.streak
        )
        HorizontalDivider(modifier = Modifier.height(32.dp).width(2.dp))
        StatItem(
            value = loginUiState.user.xp.toString(),
            label = stringResource(Res.string.xp),
            icon = Res.drawable.icon_experience
        )
    }
}

@Composable
fun StatItem(value: String, label: String, icon: DrawableResource) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ColumnScope.ProfileExitButton(onExit: () -> Unit, cardWidth: Dp) {
    Button(
        onClick = onExit,
        modifier = Modifier.width(cardWidth).align(Alignment.CenterHorizontally)
    ) {
        Text(stringResource(Res.string.exit))
    }
}

@Composable
fun ColumnScope.ProfileChangeDataButton(cardWidth: Dp, onNavigateProfileChange: () -> Unit) {
    Button(
        onClick = {
            onNavigateProfileChange()
        },
        modifier = Modifier.width(cardWidth).align(Alignment.CenterHorizontally)
    ) {
        Text(stringResource(Res.string.change_user_data))
    }
}
