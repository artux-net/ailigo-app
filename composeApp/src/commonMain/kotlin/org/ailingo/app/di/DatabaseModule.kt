package org.ailingo.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.ailingo.app.AppDatabase
import org.ailingo.app.features.auth.data.TokenRepositoryImpl
import org.ailingo.app.features.auth.domain.TokenRepository
import org.ailingo.app.features.dictionary.history.data.DictionaryRepositoryImpl
import org.ailingo.app.features.dictionary.history.di.DatabaseDriverFactory
import org.ailingo.app.features.dictionary.history.domain.DictionaryRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module


val databaseModule = module {
    single<Deferred<DictionaryRepository>>(named("dictionaryRepository")) {
        CoroutineScope(Dispatchers.Default).async {
            val driver = get<DatabaseDriverFactory>().provideDbDriver(
                AppDatabase.Schema
            )
            val db = AppDatabase(driver)
            DictionaryRepositoryImpl(db)
        }
    }
    single<Deferred<TokenRepository>>(named("tokenRepository")) {
        CoroutineScope(Dispatchers.Default).async {
            val driver = get<DatabaseDriverFactory>().provideDbDriver(
                AppDatabase.Schema
            )
            val db = AppDatabase(driver)
            TokenRepositoryImpl(db)
        }
    }
}