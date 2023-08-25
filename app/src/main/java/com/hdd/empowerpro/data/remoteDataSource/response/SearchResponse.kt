package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Search


data class SearchResponse(
    val success: Boolean? = null,
    val data: Search? = null,
    val message: String? = null
)