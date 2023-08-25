package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Comment

data class CommentResponse(
    val success: Boolean? = null,
    val data: Comment? = null,
)