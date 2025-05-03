package com.example.summitdiaryserver.routes

import com.example.summitdiaryserver.models.*
import com.example.summitdiaryserver.responses.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.testRoutes() {

    get("/api/users") {
        val users = transaction {
            UserTable.selectAll().map {
                UserResponse(
                    id = it[UserTable.id].value,
                    name = it[UserTable.name]
                )
            }
        }
        call.respond(users)
    }


    get("/hikes") {
        val hikes = transaction {
            HikeTable.selectAll().map {
                HikeResponse(
                    id = it[HikeTable.id].value,
                    title = it[HikeTable.title],
                    date = it[HikeTable.date],
                    distance = it[HikeTable.distance],
                    time = it[HikeTable.time],
                    place = it[HikeTable.placeGps] ?: "",
                    gpxPath = it[HikeTable.gpxPath],
                    userId = it[HikeTable.userId].value
                )
            }
        }
        call.respond(hikes)
    }

    get("/photos") {
        val photos = transaction {
            PhotoTable.selectAll().map {
                PhotoResponse(
                    id = it[PhotoTable.id].value,
                    location = it[PhotoTable.location] ?: "nieznana",
                    path = it[PhotoTable.path],
                    userId = it[PhotoTable.userId].value
                )
            }
        }
        call.respond(photos)
    }

    get("/hikephotos") {
        val hikePhotos = transaction {
            HikePhotoTable.selectAll().map {
                HikePhotoResponse(
                    hikeId = it[HikePhotoTable.hikeId].value,
                    photoId = it[HikePhotoTable.photoId].value
                )
            }
        }
        call.respond(hikePhotos)
    }
}
