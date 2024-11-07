package org.ailingo.app.features.chat.presentation.mobile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import org.ailingo.app.core.helper.voice.VoiceStates
import org.ailingo.app.core.helper.voice.VoiceToTextParser
import org.ailingo.app.features.chat.data.model.Message
import org.ailingo.app.features.chat.presentation.ChatScreenViewModel

@Composable
fun ChatScreenMobile(
    voiceToTextParser: VoiceToTextParser,
    chatTextField: TextFieldValue,
    chatState: List<Message>,
    listState: LazyListState,
    voiceState: State<VoiceStates>,
    chatViewModel: ChatScreenViewModel,
    isActiveJob: State<Boolean>,
    onChatTextField: (TextFieldValue) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomUserMessageBoxMobile(
                chatTextField,
                voiceToTextParser,
                voiceState,
                chatState,
                listState,
                chatViewModel,
                isActiveJob.value,
                onChatTextField = onChatTextField
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chatState) { message ->
                MessageItemMobile(message)
            }
        }
    }
}
