package org.ailingo.app.core.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.ailingologowithoutbackground
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.streak
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ailingo.app.features.login.presentation.LoginUiState
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.theme.inversePrimaryLight
import org.ailingo.app.theme.primaryContainerLight
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithProfile(modifier: Modifier = Modifier, loginViewModel: LoginViewModel) {
    TopAppBar(
        modifier = modifier,
        title = {
            Box(
                modifier = Modifier.height(64.dp).width(350.dp).offset((-16).dp, 0.dp)
                    .background(Color.White)
            )
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.height(64.dp).fillMaxWidth()) {
                Icon(
                    painter = painterResource(Res.drawable.ailingologowithoutbackground),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.height(40.dp)
                )
            }
        },
        actions = {
            when (val loginState = loginViewModel.loginState.value) {
                LoginUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is LoginUiState.Success -> {
                    if (loginState.avatar?.isNotEmpty() == true) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp).padding(8.dp)
                        ) {
                            AsyncImage(
                                model = loginState.avatar,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
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
                                if (loginState.avatar?.isNotEmpty() == true) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = loginState.avatar,
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
                                            text = loginState.login?.first()?.uppercase()
                                                .toString(),
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
                                            text = loginState.login?.first()?.uppercase()
                                                .toString(),
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
                                            loginState.coins.toString(),
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
                                            loginState.streak.toString(),
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
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
