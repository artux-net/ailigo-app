package org.ailingo.app.features.registration.presentation.uploadavatar

import ailingo.composeapp.generated.resources.ArrowForwardIOS
import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.back_input_fields
import ailingo.composeapp.generated.resources.choose_image
import ailingo.composeapp.generated.resources.continue_app
import ailingo.composeapp.generated.resources.continue_with_default_image
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.delete_avatar
import ailingo.composeapp.generated.resources.lets_add_your_avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.ailingo.app.UploadAvatarForPhone
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.ailingo.app.core.utils.windowinfo.util.PlatformName
import org.ailingo.app.features.registration.data.model.UserRegistrationData
import org.ailingo.app.getPlatformName
import org.ailingo.app.selectImageWebAndDesktop
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterUploadAvatarEmpty(
    login: String,
    password: String,
    email: String,
    name: String,
    onNavigateToRegisterScreen: () -> Unit,
    uploadAvatarViewModel: UploadAvatarViewModel
) {
    var savedPhoto by remember {
        mutableStateOf("")
    }
    if (getPlatformName() == PlatformName.Android) {
        UploadAvatarForPhone(
            uploadAvatarViewModel,
            login,
            password,
            email,
            name,
            onNavigateToRegisterScreen = onNavigateToRegisterScreen
        )
    } else {
        val imageState = uploadAvatarViewModel.imageState.collectAsState()
        var base64Image by remember {
            mutableStateOf<String?>(null)
        }
        val scope = rememberCoroutineScope()
        LaunchedEffect(base64Image) {
            if (base64Image?.isNotEmpty() == true) {
                uploadAvatarViewModel.onEvent(UploadAvatarEvent.OnUploadImage(base64Image!!))
            }
        }
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    Text(
                        stringResource(Res.string.lets_add_your_avatar),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(300.dp)
                            ) {
                                Card(
                                    shape = CircleShape,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    when (imageState.value) {
                                        UploadImageUiState.EmptyImage -> {
                                            if (savedPhoto.isNotEmpty()) {
                                                AsyncImage(
                                                    model = savedPhoto,
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Image(
                                                    painter = painterResource(Res.drawable.defaultProfilePhoto),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }

                                        is UploadImageUiState.Error -> {
                                            SelectionContainer {
                                                Box(
                                                    modifier = Modifier.fillMaxSize()
                                                        .padding(start = 16.dp, end = 16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        (imageState.value as UploadImageUiState.Error).message,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }

                                        UploadImageUiState.LoadingImage -> {
                                            LoadingScreen()
                                        }

                                        is UploadImageUiState.Success -> {
                                            savedPhoto =
                                                ((imageState.value as UploadImageUiState.Success).uploadImageResponse.data.image.url)
                                            AsyncImage(
                                                model = savedPhoto,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ElevatedButton(onClick = {
                                onNavigateToRegisterScreen()
                            }, shape = MaterialTheme.shapes.small) {
                                Text(stringResource(Res.string.back_input_fields))
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = ButtonDefaults.MinHeight + 16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        selectImageWebAndDesktop(
                                            scope = scope,
                                            callback = {
                                                base64Image = it
                                            }
                                        )
                                    }
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    stringResource(Res.string.choose_image),
                                    color = Color.Black
                                )
                            }

                            if (imageState.value is UploadImageUiState.Success && savedPhoto.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        savedPhoto = ""
                                        base64Image = null
                                        uploadAvatarViewModel.onEvent(UploadAvatarEvent.OnBackToEmptyUploadAvatar)
                                    },
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        stringResource(Res.string.delete_avatar),
                                        color = Color.Black
                                    )
                                }
                            }

                            if (imageState.value is UploadImageUiState.EmptyImage && savedPhoto.isEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ArrowForwardIOS),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        stringResource(Res.string.continue_with_default_image),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            if (imageState.value !is UploadImageUiState.LoadingImage) {
                                OutlinedButton(onClick = {
                                    if (imageState.value is UploadImageUiState.Success && savedPhoto.isNotEmpty()) {
                                        uploadAvatarViewModel.onEvent(
                                            UploadAvatarEvent.RegisterUser(
                                                UserRegistrationData(
                                                    login = login,
                                                    password = password,
                                                    email = email,
                                                    name = name,
                                                    avatar = savedPhoto
                                                )
                                            )
                                        )
                                    } else {
                                        uploadAvatarViewModel.onEvent(
                                            UploadAvatarEvent.RegisterUser(
                                                UserRegistrationData(
                                                    login = login,
                                                    password = password,
                                                    email = email,
                                                    name = name,
                                                    avatar = ""
                                                )
                                            )
                                        )
                                    }
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(stringResource(Res.string.continue_app))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
