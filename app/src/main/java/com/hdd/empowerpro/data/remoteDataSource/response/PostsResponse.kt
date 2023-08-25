package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Post


data class PostsResponse(
    val success: Boolean? = null,
    val data: MutableList<Post>? = null,
)