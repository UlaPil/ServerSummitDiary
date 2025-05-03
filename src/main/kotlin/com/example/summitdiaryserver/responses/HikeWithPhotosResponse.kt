package com.example.summitdiaryserver.responses

import kotlinx.serialization.Serializable

@Serializable
data class HikeWithPhotosResponse(
    val id: Int,
    val title: String,
    val date: String,
    val distance: Double,
    val time: String,
    val placeId: Int,
    val placeName: String?,
    val gpxPath: String,
    val userId: Int,
    val photos: List<PhotoResponse>
)
