package org.ailingo.app.features.profileupdate.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.back
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.ailingo.app.core.utils.presentation.ErrorScreen
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileUpdateScreen(
    modifier: Modifier = Modifier,
    profileUpdateViewModel: ProfileUpdateViewModel,
    onProfileUpdate: () -> Unit,
    onNavigateProfileScreen: () -> Unit,
    name: String,
    email: String,
    avatar: String
) {
    val profileUpdateUiState =
        profileUpdateViewModel.profileUpdateUiState.collectAsStateWithLifecycle()

    LaunchedEffect(profileUpdateUiState.value) {
        if (profileUpdateUiState.value is ProfileUpdateUiState.Success) {
            onProfileUpdate()
            delay(1000L)
            onNavigateProfileScreen()
        }
    }

    when (val state = profileUpdateUiState.value) {
        ProfileUpdateUiState.Empty -> {
            ProfileUpdateContent(
                profileUpdateViewModel,
                name,
                email,
                avatar
            )
        }

        is ProfileUpdateUiState.Error -> {
            ErrorScreen(errorMessage = state.message, onButtonClick = {
                profileUpdateViewModel.backToEmptyState()
            }, buttonMessage = stringResource(Res.string.back))
        }

        ProfileUpdateUiState.Loading -> {
            LoadingScreen()
        }

        ProfileUpdateUiState.Success -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("SUCCESS UPDATE", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

@Composable
fun ProfileUpdateContent(
    profileUpdateViewModel: ProfileUpdateViewModel,
    name: String,
    email: String,
    avatar: String,
    modifier: Modifier = Modifier
) {

    var newName by remember {
        mutableStateOf(name)
    }
    var newEmail by remember {
        mutableStateOf(email)
    }
    var newAvatar by remember {
        mutableStateOf(avatar)
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newAvatar,
            onValueChange = { newAvatar = it },
            label = { Text("Avatar URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            profileUpdateViewModel.updateProfile(newName, newEmail, newAvatar)
        }
        ) {
            Text("Update Profile")
        }
    }
}
