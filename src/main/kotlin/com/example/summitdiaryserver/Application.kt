
package com.example.summitdiaryserver

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import com.example.summitdiaryserver.models.DatabaseFactory
import com.example.summitdiaryserver.routes.*
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticRootFolder
import kotlinx.serialization.json.Json
import java.io.File


fun main() {
    embeddedServer(Netty, port = 8888) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        environment.monitor.subscribe(ApplicationStarted) {
            val uploadsPath = File(System.getProperty("user.dir")).resolve("uploads")
            println("Uploads folder absolute path: ${uploadsPath.absolutePath}")
            println("Photos exists: ${uploadsPath.resolve("photos").exists()}")
            println("GPX exists: ${uploadsPath.resolve("gpx").exists()}")
        }

        DatabaseFactory.init()
        routing {
            userRoutes()
            hikeRoutes()
            testRoutes()
            static("/uploads") {
                staticRootFolder = File(System.getProperty("user.dir")).resolve("uploads")
                files("photos")
                files("gpx")
            }
        }
    }.start(wait = true)

}

