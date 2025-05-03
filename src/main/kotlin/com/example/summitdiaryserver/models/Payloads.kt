
package com.example.summitdiaryserver.models

import kotlinx.serialization.Serializable

@Serializable
data class HikeWithPhotosPayload(
    val userId: Int,
    val hike: HikePayload,
    val photos: List<PhotoPayload>
)

@Serializable
data class HikePayload(
    val title: String,
    val date: String,
    val distance: Double,
    val time: String,
    val placeName: String?,
    val placeGps: String?,
    val gpxPath: String
)

@Serializable
data class PhotoPayload(
    val localPhotoId: Long,
    val path: String,
    val location: String?
)
