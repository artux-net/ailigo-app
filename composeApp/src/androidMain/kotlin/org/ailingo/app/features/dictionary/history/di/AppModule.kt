package org.ailingo.app.features.dictionary.history.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.ailingo.app.DriverFactory
import org.ailingo.app.features.dictionary.history.data.DictionaryRepositoryImpl
import org.ailingo.app.features.dictionary.history.domain.DictionaryRepository
import org.ailingo.composeApp.database.HistoryDictionaryDatabase

actual class AppModule(
    private val context: Context
) {
    actual val dictionaryRepository: Deferred<DictionaryRepository> = CoroutineScope(Dispatchers.Default).async {
        DictionaryRepositoryImpl(
            db = HistoryDictionaryDatabase(
                driver = DriverFactory(context).createDriver()
            )
        )
    }
}
