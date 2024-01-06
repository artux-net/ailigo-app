package org.ailingo.app.feature_chat.presentation

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
import org.ailingo.app.core.util.VoiceStates
import org.ailingo.app.core.util.VoiceToTextParser
import org.ailingo.app.feature_chat.data.model.Message

@Composable
fun ChatScreenDesktop(
    voiceToTextParser: VoiceToTextParser,
    chatTextField: TextFieldValue,
    chatState: List<Message>,
    listState: LazyListState,
    voiceState: State<VoiceStates>,
    chatViewModel: ChatViewModel,
    isActiveJob: State<Boolean>,
    onChatTextField: (TextFieldValue) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomUserMessageBox(
                chatTextField = chatTextField,
                voiceToTextParser = voiceToTextParser,
                voiceState = voiceState,
                messages = chatState,
                listState = listState,
                chatViewModel = chatViewModel,
                isActiveJob = isActiveJob.value,
                onChatTextField = onChatTextField
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize().padding(padding)
        ) {
            items(chatState) { message ->
                MessageItemDesktop(message)
            }
        }
    }
}