package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.Company
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.CompanyResponse
import com.hdd.empowerpro.data.remoteDataSource.response.ImageResponse
import com.hdd.empowerpro.data.remoteDataSource.services.CompanyServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CompanyRepository : HttpRequestNetworkCall() {
    private val companyService = ServiceBuilder.buildService(CompanyServices::class.java)

    suspend fun addCompanyDetail(image: MultipartBody.Part,companyName: RequestBody, companyDescription: RequestBody,
                                    companyAddress: RequestBody,companyOpeningTime: RequestBody,companyClosingTime: RequestBody,
                                    companyPhone: RequestBody,): CompanyResponse {
        return myHttpRequestNetworkCall {
            companyService.addCompanyDetail(ServiceBuilder.token!!,image,companyName, companyDescription,companyAddress,companyOpeningTime,companyClosingTime,companyPhone)
        }
    }

    suspend fun getCompanyById(companyId: String): CompanyResponse {
        return myHttpRequestNetworkCall {
            companyService.getCompanyById(ServiceBuilder.token!!,companyId)
        }
    }

    suspend fun updateCompanyDetails(companyId: String,company: Company)
            : CompanyResponse {
        return myHttpRequestNetworkCall {
            companyService.updateCompanyDetails(ServiceBuilder.token!!,companyId,company)
        }
    }

    suspend fun updateCompanyImage(companyId: String,body: MultipartBody.Part)
            : ImageResponse {
        return myHttpRequestNetworkCall {
            companyService.updateCompanyImage(ServiceBuilder.token!!,companyId,body)
        }
    }

    suspend fun updateCompanyCoverImage(companyId: String,body: MultipartBody.Part)
            : ImageResponse {
        return myHttpRequestNetworkCall {
            companyService.updateCompanyCoverImage(ServiceBuilder.token!!,companyId,body)
        }
    }

    suspend fun updateCompanyDetailsWithoutImage(companyId: String,company: Company): CompanyResponse {
        return myHttpRequestNetworkCall {
            companyService.updateCompanyDetailsWithoutImage(ServiceBuilder.token!!,companyId, company)
        }
    }
}