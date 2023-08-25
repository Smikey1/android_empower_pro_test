package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Job


data class JobsResponse(
    val success: Boolean? = null,
    val data: MutableList<Job>? = null,
)