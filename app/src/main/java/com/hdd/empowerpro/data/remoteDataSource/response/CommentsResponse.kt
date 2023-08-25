package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Comment


data class CommentsResponse(
    val success: Boolean? = null,
    val data: MutableList<Comment>? = null,
)