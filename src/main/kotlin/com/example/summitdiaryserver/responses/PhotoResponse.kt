package com.example.summitdiaryserver.responses

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResponse(
    val id: Int,
    val location: String,
    val path: String,
    val userId: Int
)