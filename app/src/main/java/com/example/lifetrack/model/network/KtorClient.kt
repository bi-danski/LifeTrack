// File: /home/revil/AndroidStudioProjects/LifeTrack/app/src/main/java/com/example/lifetrack/network/KtorClient.kt
package com.example.lifetrack.model.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class KtorClientFactory {
    fun create(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }
}
