package org.ailingo.app.core.presentation.topappbar

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.ailingologowithoutbackground
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.logo
import ailingo.composeapp.generated.resources.streak
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.features.login.presentation.LoginUiState
import org.ailingo.app.theme.inversePrimaryLight
import org.ailingo.app.theme.primaryContainerLight
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithProfile(
    modifier: Modifier = Modifier,
    loginState: LoginUiState,
    windowInfo: WindowInfo
) {
    TopAppBar(
        modifier = modifier,
        title = {
            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.DesktopWindowInfo) {
                Box(
                    modifier = Modifier
                        .height(TopAppBarDefaults.TopAppBarExpandedHeight)
                        .width(350.dp)
                        .offset((-16).dp, 0.dp) //need for delete standard padding title
                        .background(Color.White)
                )
                Icon(
                    painter = painterResource(Res.drawable.ailingologowithoutbackground),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.padding(top = 10.dp).height(40.dp)
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.height(40.dp)
                )
            }
        },
        actions = {
            when (loginState) {
                LoginUiState.Loading -> {
                    LoadingScreen()
                }

                is LoginUiState.Success -> {
                    Card(
                        modifier = Modifier.padding(8.dp),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = inversePrimaryLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(4.dp).padding(start = 2.dp).padding(end = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (loginState.user.avatar.isNotEmpty()) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = loginState.user.avatar,
                                        contentDescription = "avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .border(
                                                0.5.dp,
                                                Color.Black,
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = loginState.user.login.first().uppercase(),
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.defaultProfilePhoto),
                                        contentDescription = "avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .border(
                                                0.5.dp,
                                                Color.Black,
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = loginState.user.login.first().uppercase(),
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = primaryContainerLight),
                                shape = CircleShape,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(4.dp).padding(end = 4.dp)
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.coins),
                                        contentDescription = "money",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        loginState.user.coins.toString(),
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = primaryContainerLight),
                                shape = CircleShape,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(4.dp).padding(end = 4.dp)
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.streak),
                                        contentDescription = "streak",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        loginState.user.streak.toString(),
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    )
}
