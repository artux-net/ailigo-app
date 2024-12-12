package org.ailingo.app.di

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.connection_timeout
import ailingo.composeapp.generated.resources.could_not_connect
import ailingo.composeapp.generated.resources.request_timeout
import ailingo.composeapp.generated.resources.unexpected_error
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                socketTimeoutMillis = 15000
                connectTimeoutMillis = 15000
            }
        }
    }

    single<NetworkErrorMapper> {
        object : NetworkErrorMapper {
            override suspend fun mapError(throwable: Throwable): String {
                return when (throwable) {
                    is HttpRequestTimeoutException -> getString(Res.string.request_timeout)
                    is ConnectTimeoutException -> getString(Res.string.could_not_connect)
                    is SocketTimeoutException -> getString(Res.string.connection_timeout)
                    else -> getString(Res.string.unexpected_error, throwable.message.toString() ?: "")
                }
            }
        }
    }
}

interface NetworkErrorMapper {
    suspend fun mapError(throwable: Throwable): String
}