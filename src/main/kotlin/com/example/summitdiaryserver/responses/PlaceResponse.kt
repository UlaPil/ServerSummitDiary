package com.example.summitdiaryserver.responses

import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    val id: Int,
    val name: String,
    val gps: String,
    val userId: Int
)