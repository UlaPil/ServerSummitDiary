package com.example.summitdiaryserver.routes

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val hike_id: Int,
    val photo_id_map: Map<String, Int>
)