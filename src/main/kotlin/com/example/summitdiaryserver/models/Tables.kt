
package com.example.summitdiaryserver.models

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("Users") {
    val name = varchar("name", 255)
}

object HikeTable : IntIdTable("Hikes") {
    val userId = reference("user_id", UserTable)
    val title = varchar("title", 255)
    val date = varchar("date", 255)
    val distance = double("distance")
    val time = varchar("time", 255)
    val placeName = varchar("place_name", 255).nullable()
    val placeGps = varchar("place_gps", 255).nullable()
    val gpxPath = varchar("gpx_path", 255)
}

object PhotoTable : IntIdTable("Photos") {
    val userId = reference("user_id", UserTable)
    val path = varchar("path", 255)
    val location = varchar("location", 255).nullable()
}

object HikePhotoTable : IntIdTable("HikePhotos") {
    val hikeId = reference("hike_id", HikeTable)
    val photoId = reference("photo_id", PhotoTable)
}
