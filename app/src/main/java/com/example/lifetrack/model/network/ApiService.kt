package com.example.lifetrack.model.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*

class ApiService(private val client: HttpClient) {
    suspend fun testService(userId: String): HttpResponse{
        return try {
            client.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.lifetrack.app"
                    path("/v1/chats", userId)
                }
            }
        }catch (e: Exception) {
            throw e
        }
    }


}