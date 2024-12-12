package org.ailingo.app.features.dictionary.main.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.definitions
import ailingo.composeapp.generated.resources.history_of_search
import ailingo.composeapp.generated.resources.no_definitions
import ailingo.composeapp.generated.resources.usage_examples
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ailingo.app.core.utils.presentation.ErrorScreen
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.ailingo.app.features.dictionary.history.domain.HistoryDictionaryUiState
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DictionaryScreen(
    dictionaryViewModel: DictionaryViewModel
) {
    val dictionaryState = dictionaryViewModel.dictionaryUiState.collectAsState()
    val historyDictionaryState = dictionaryViewModel.historyOfDictionaryState.collectAsState()
    val favoriteDictionaryState =
        dictionaryViewModel.favoriteWords.collectAsStateWithLifecycle().value
    val textFieldValue = rememberSaveable { mutableStateOf("") }
    val active = remember {
        mutableStateOf(false)
    }
    val searchBarHeight = remember { mutableStateOf(0) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            stickyHeader {
                SearchTextFieldDictionary(
                    dictionaryViewModel = dictionaryViewModel,
                    textFieldValue = textFieldValue,
                    onTextFieldValueChange = { newTextFieldValue ->
                        textFieldValue.value = newTextFieldValue
                    },
                    active = active,
                    searchBarHeight = searchBarHeight
                ) { searchWord ->
                    dictionaryViewModel.onEvent(
                        DictionaryScreenEvents.SearchWordDefinition(
                            searchWord
                        )
                    )
                }
            }
            if (dictionaryState.value is DictionaryUiState.Empty) {
                when (val historyState = historyDictionaryState.value) {
                    is HistoryDictionaryUiState.Error -> {
                        item {
                            ErrorScreen(errorMessage = historyState.message)
                        }
                    }

                    HistoryDictionaryUiState.Loading -> {
                        item {
                            LoadingScreen()
                        }
                    }

                    is HistoryDictionaryUiState.Success -> {
                        items(historyState.history.reversed()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(14.dp).clickable {
                                    textFieldValue.value = it.text
                                    dictionaryViewModel.onEvent(
                                        DictionaryScreenEvents.SearchWordDefinition(
                                            it.text
                                        )
                                    )
                                    active.value = false
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.padding(end = 10.dp),
                                    imageVector = Icons.Default.History,
                                    contentDescription = null
                                )
                                Text(text = it.text)
                            }
                        }

                    }
                }
            }

            if (dictionaryState.value is DictionaryUiState.Success) {
                val response = (dictionaryState.value as DictionaryUiState.Success).response
                val responseForExamples =
                    (dictionaryState.value as DictionaryUiState.Success).responseExample
                val listOfExamples = responseForExamples?.flatMap {
                    it.meanings.flatMap { meaning ->
                        meaning.definitions.mapNotNull { def ->
                            def.example
                        }
                    }
                }
                val listOfDefinitions = responseForExamples?.flatMap {
                    it.meanings.flatMap { meaning ->
                        meaning.definitions.map { def ->
                            def.definition
                        }
                    }
                }
                if (response.def?.isNotEmpty() == true) {
                    items(response.def) { definition ->
                        DefinitionRowInfo(definition, responseForExamples, favoriteDictionaryState, dictionaryViewModel)
                    }
                    item {
                        if (listOfExamples?.isNotEmpty() == true) {
                            Text(
                                stringResource(Res.string.usage_examples),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    item {
                        ListOfExample(listOfExamples, textFieldValue.value)
                    }
                    item {
                        if (listOfDefinitions?.isNotEmpty() == true) {
                            Text(
                                stringResource(Res.string.definitions),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    item {
                        ListOfDefinitions(listOfDefinitions)
                    }
                } else {
                    item {
                        Text(stringResource(Res.string.no_definitions))
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = dictionaryState.value) {
                DictionaryUiState.Empty -> {
                    if (historyDictionaryState.value is HistoryDictionaryUiState.Success && (historyDictionaryState.value as HistoryDictionaryUiState.Success).history.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .padding(top = searchBarHeight.value.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(Res.string.history_of_search))
                        }
                    }
                }

                is DictionaryUiState.Error -> {
                    ErrorScreen(
                        errorMessage = state.message,
                        modifier = Modifier.padding(top = searchBarHeight.value.dp)
                    )
                }

                DictionaryUiState.Loading -> {
                    LoadingScreen(modifier = Modifier.padding(top = searchBarHeight.value.dp))
                }

                else -> {}
            }
        }
    }
}
