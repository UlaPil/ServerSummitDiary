package com.example.summitdiaryserver.responses

import kotlinx.serialization.Serializable

@Serializable
data class HikePhotoResponse(
    val hikeId: Int,
    val photoId: Int
)