package org.ailingo.app.features.dictionary.main.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Volume2
import org.ailingo.app.features.dictionary.examples.data.model.WordInfoItem
import org.ailingo.app.features.dictionary.main.data.model.Def
import org.ailingo.app.features.dictionary.main.presentation.utils.getPartOfSpeechLabel
import org.ailingo.app.playSound

@Composable
fun DefinitionRowInfo(
    definition: Def,
    responseForExamples: List<WordInfoItem>?,
    favoriteDictionaryState: List<String>,
    dictionaryViewModel: DictionaryViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            definition.text,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.width(8.dp))
        // val fontFamilyForTranscription: FontFamily = fontFamilyResource(SharedRes.fonts.NotoSans.light)
        // Text("[" + definition.ts + "]", fontFamily = fontFamilyForTranscription, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(getPartOfSpeechLabel(definition.pos))
        val listOfAllAudio = responseForExamples?.flatMap { wordInfoItem ->
            wordInfoItem.phonetics
                .mapNotNull {
                    it.audio.takeIf { audio ->
                        audio?.isNotBlank() ?: false
                    }
                }
        }
        val firstNonEmptyAudio = listOfAllAudio?.firstOrNull()
        if (firstNonEmptyAudio != null) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                IconButton(onClick = {
                    playSound(firstNonEmptyAudio)
                }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = FeatherIcons.Volume2,
                        contentDescription = null
                    )
                }
            }
        }
        if (favoriteDictionaryState.contains(definition.text)) {
            IconButton(onClick = {
                dictionaryViewModel.onEvent(DictionaryScreenEvents.RemoveFromFavorites(
                    definition.text
                ))
            }) {
                Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = Color.Red)
            }
        } else {
            IconButton(onClick = {
                dictionaryViewModel.onEvent(DictionaryScreenEvents.AddToFavorites(
                    definition.text
                ))
            }) {
                Icon(imageVector = Icons.Outlined.Favorite, contentDescription = null)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    definition.tr.forEachIndexed { index, tr ->
        DefinitionEntry(index, tr)
    }
}
