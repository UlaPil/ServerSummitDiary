
package com.example.summitdiaryserver.routes

import com.example.summitdiaryserver.models.*
import com.example.summitdiaryserver.responses.HikeWithPhotosResponse
import com.example.summitdiaryserver.responses.PhotoResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider

import java.io.File

import org.slf4j.LoggerFactory

fun Route.hikeRoutes() {
    val logger = LoggerFactory.getLogger("HikeRoutes")
//    get("/api/debug/reset-db") {
//        transaction {
//            HikePhotoTable.deleteAll()
//            PhotoTable.deleteAll()
//            HikeTable.deleteAll()
//            UserTable.deleteAll()
//
//            exec("DELETE FROM sqlite_sequence WHERE name='HikePhotos'")
//            exec("DELETE FROM sqlite_sequence WHERE name='Photos'")
//            exec("DELETE FROM sqlite_sequence WHERE name='Hikes'")
//            exec("DELETE FROM sqlite_sequence WHERE name='Users'")
//        }
//        call.respondText("Database and sequences reset!")
//    }
    post("/api/hikes") {
        val multipart = call.receiveMultipart()
        var metadataJson = ""
        val photos = mutableListOf<Pair<String, String>>()
        var gpxPath: String = ""

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "metadata") {
                        metadataJson = part.value
                        logger.info("Received metadata part")
                    }
                }
                is PartData.FileItem -> {
                    val fileName = part.originalFileName ?: "unknown"
                    val folder = when (part.name) {
                        "photo" -> "uploads/photos"
                        "gpx" -> "uploads/gpx"
                        else -> "uploads/other"
                    }
                    val filePath = "$folder/${System.currentTimeMillis()}_$fileName"
                    val file = File(filePath)
                    file.parentFile.mkdirs()
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    when (part.name) {
                        "photo" -> photos.add(fileName to filePath)
                        "gpx" -> gpxPath = filePath
                    }
                    logger.info("Saved $fileName to $filePath")
                }
                else -> {}
            }
            part.dispose()
        }

        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        val payload = json.decodeFromString<HikeWithPhotosPayload>(metadataJson)
        var hikeId: Int = 0
        val photoIdMap = mutableMapOf<Long, Int>()

        transaction {
            hikeId = HikeTable.insertAndGetId {
                it[userId] = payload.userId
                it[title] = payload.hike.title
                it[date] = payload.hike.date
                it[distance] = payload.hike.distance
                it[time] = payload.hike.time
                it[placeName] = payload.hike.placeName
                it[placeGps] = payload.hike.placeGps
                it[HikeTable.gpxPath] = gpxPath
            }.value

            payload.photos.forEachIndexed { index, photo ->
                val (originalName, savedPath) = photos[index]
                val photoId = PhotoTable.insertAndGetId {
                    it[userId] = payload.userId
                    it[path] = savedPath
                    it[location] = photo.location
                }.value

                photoIdMap[photo.localPhotoId] = photoId

                HikePhotoTable.insert {
                    it[HikePhotoTable.hikeId] = hikeId
                    it[HikePhotoTable.photoId] = photoId
                }
            }
        }

        val photoIdMapStr = photoIdMap.mapKeys { it.key.toString() }
        call.respond(HttpStatusCode.Created, Response(
            hike_id = hikeId,
            photo_id_map = photoIdMapStr
        ))
    }
    get("/api/hikes/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        val response = transaction {
            val hikeRow = HikeTable.select { HikeTable.id eq id }.singleOrNull()
            if (hikeRow == null) return@transaction null

            val photos = (HikePhotoTable innerJoin PhotoTable)
                .select { HikePhotoTable.hikeId eq id }
                .map {
                    PhotoResponse(
                        id = it[PhotoTable.id].value,
                        location = it[PhotoTable.location] ?: "",
                        path = "/uploads/photos/${it[PhotoTable.path].substringAfterLast("/")}",
                        userId = it[PhotoTable.userId].value
                    )
                }

            HikeWithPhotosResponse(
                id = hikeRow[HikeTable.id].value,
                title = hikeRow[HikeTable.title],
                date = hikeRow[HikeTable.date],
                distance = hikeRow[HikeTable.distance],
                time = hikeRow[HikeTable.time],
                placeName = hikeRow[HikeTable.placeName],
                placeGps = hikeRow[HikeTable.placeGps],
                gpxPath = "/uploads/gpx/${hikeRow[HikeTable.gpxPath].substringAfterLast("/")}",
                userId = hikeRow[HikeTable.userId].value,
                photos = photos
            )
        }

        if (response != null) {
            call.respond(response)
        } else {
            call.respond(HttpStatusCode.NotFound, "Hike not found")
        }
    }

    get("/api/gpx/{filename}") {
        val filename = call.parameters["filename"]
        if (filename.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Filename missing")
            return@get
        }

        val file = File(System.getProperty("user.dir"))
            .resolve("uploads/gpx")
            .resolve(filename)

        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound, "File not found")
            return@get
        }

        call.respondFile(file)
    }

    get("/api/photo/{filename}") {
        val filename = call.parameters["filename"]
        if (filename.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Filename missing")
            return@get
        }

        val file = File(System.getProperty("user.dir"))
            .resolve("uploads/photos")
            .resolve(filename)

        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound, "File not found")
            return@get
        }

        call.respondFile(file)
    }



}

