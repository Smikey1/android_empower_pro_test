package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.data.remoteDataSource.response.JobResponse
import com.hdd.empowerpro.data.remoteDataSource.response.JobsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface JobServices {

    @Multipart
    @POST("job")
    suspend fun addJob(
        @Header("Authorization") token: String,
        @Part("prevJobId") prevJobId: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
    ): Response<JobResponse>

    @PATCH("job/no-image/{id}")
    suspend fun updateJobWithoutImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body job: Job,
    ): Response<JobResponse>

    @GET("job/{id}")
    suspend fun getJobById(
        @Header("Authorization") token: String,
        @Path("id",
        ) id: String): Response<JobResponse>

    @PATCH("job/detail/{id}")
    suspend fun updateJobDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body job: Job
    ): Response<JobResponse>


    @PATCH("job/direction/{id}")
    suspend fun updateJobDirection(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body job: Job
    ): Response<JobResponse>

    @PATCH("job/hashtag/{id}")
    suspend fun updateJobHashtag(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body job: Job
    ): Response<JobResponse>

    @DELETE("job/discard/{id}")
    suspend fun discardJob(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<JobResponse>

    @POST("job/share/{id}")
    suspend fun shareJob(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<JobResponse>

    @POST("job/ok/{id}")
    suspend fun postJob(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<JobResponse>

    @POST("job/archive/{id}")
    suspend fun archivedJob(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<JobResponse>

    @DELETE("job/deleteArchived/{id}")
    suspend fun deleteArchived(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<JobResponse>

    @GET("job/archive")
    suspend fun viewArchivedJob(
        @Header("Authorization") token: String,
    ): Response<JobsResponse>

    @FormUrlEncoded
    @POST("reportJob/{id}")
    suspend fun reportJob(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("reason") reason:String
    ): Response<JobResponse>

    @DELETE("job/{id}")
    suspend fun deleteJob(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<JobResponse>
}