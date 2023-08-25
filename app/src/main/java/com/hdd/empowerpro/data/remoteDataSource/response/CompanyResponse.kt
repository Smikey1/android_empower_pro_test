package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.Company


data class CompanyResponse(
    val success: Boolean? = null,
    val data: Company? = null,
)