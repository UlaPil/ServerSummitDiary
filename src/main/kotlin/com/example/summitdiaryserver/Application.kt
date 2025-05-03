
package com.example.summitdiaryserver

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import com.example.summitdiaryserver.models.DatabaseFactory
import com.example.summitdiaryserver.routes.*
import kotlinx.serialization.json.Json


fun main() {
    embeddedServer(Netty, port = 8888) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        DatabaseFactory.init()
        routing {
            userRoutes()
            hikeRoutes()
            testRoutes()
        }
    }.start(wait = true)
}

