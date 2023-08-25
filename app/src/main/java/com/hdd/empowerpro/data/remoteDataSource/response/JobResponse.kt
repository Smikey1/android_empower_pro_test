package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Job


data class JobResponse(
    val success: Boolean? = null,
    val data: Job? = null,
)