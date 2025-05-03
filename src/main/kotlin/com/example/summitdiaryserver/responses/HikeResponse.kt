package com.example.summitdiaryserver.responses

import kotlinx.serialization.Serializable

@Serializable
data class HikeResponse(
    val id: Int,
    val title: String,
    val date: String,
    val distance: Double,
    val time: String,
    val place: String,
    val gpxPath: String,
    val userId: Int
)