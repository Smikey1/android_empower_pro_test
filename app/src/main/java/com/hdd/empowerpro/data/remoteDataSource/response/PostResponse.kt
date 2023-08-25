package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Post


data class PostResponse(
    val success: Boolean? = null,
    val data: Post? = null,
    val accessToken: String? = null
)