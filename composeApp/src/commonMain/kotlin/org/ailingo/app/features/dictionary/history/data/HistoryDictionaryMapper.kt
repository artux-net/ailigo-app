package org.ailingo.app.features.dictionary.history.data

import org.ailingo.app.HistoryDictionaryEntity
import org.ailingo.app.features.dictionary.history.domain.HistoryDictionary

fun HistoryDictionaryEntity.toHistoryDictionary(): HistoryDictionary {
    return HistoryDictionary(
        id = id,
        text = text
    )
}
