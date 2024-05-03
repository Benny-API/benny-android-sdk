package com.bennyapi.transfer.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

internal object HttpClientFactory {
    fun create(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(globalJson)
            }
        }
    }
}
