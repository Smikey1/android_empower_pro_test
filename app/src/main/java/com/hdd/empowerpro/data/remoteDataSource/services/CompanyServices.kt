package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.empowerpro.data.models.Company
import com.hdd.empowerpro.data.remoteDataSource.response.CompanyResponse
import com.hdd.empowerpro.data.remoteDataSource.response.ImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CompanyServices {

    @Multipart
    @POST("company")
    suspend fun addCompanyDetail(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("address") address: RequestBody,
        @Part("openingTime") openingTime: RequestBody,
        @Part("closingTime") closingTime: RequestBody,
        @Part("phone") phone: RequestBody,
    ): Response<CompanyResponse>
    
    @GET("company/{id}")
    suspend fun getCompanyById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<CompanyResponse>

    @PATCH("company/no-image/{id}")
    suspend fun updateCompanyDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body company: Company
    ): Response<CompanyResponse>

    @Multipart
    @PATCH("company/company-image/{id}")
    suspend fun updateCompanyImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Response<ImageResponse>

    @Multipart
    @PATCH("company/cover-image/{id}")
    suspend fun updateCompanyCoverImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Response<ImageResponse>

    @PATCH("company/no-image/{id}")
    suspend fun updateCompanyDetailsWithoutImage(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Body company: Company,
    ): Response<CompanyResponse>
}