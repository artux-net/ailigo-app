package org.ailingo.app.features.dictionary.main.presentation

import org.ailingo.app.features.dictionary.history.domain.HistoryDictionary
import org.ailingo.app.features.dictionary.predictor.data.PredictorRequest

sealed class DictionaryScreenEvents {
    data class SearchWordDefinition(val word: String) : DictionaryScreenEvents()
    data class PredictNextWords(val request: PredictorRequest) : DictionaryScreenEvents()
    data class SaveSearchedWord(val word: HistoryDictionary) : DictionaryScreenEvents()
    data class DeleteFromHistory(val id: Long) : DictionaryScreenEvents()
    data class AddToFavorites(val word: String) : DictionaryScreenEvents()
    data class RemoveFromFavorites(val word: String) : DictionaryScreenEvents()
}
