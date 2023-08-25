package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.JobResponse
import com.hdd.empowerpro.data.remoteDataSource.response.JobsResponse
import com.hdd.empowerpro.data.remoteDataSource.services.JobServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class JobRepository : HttpRequestNetworkCall() {
    private val jobService = ServiceBuilder.buildService(JobServices::class.java)

    suspend fun addJob(image: MultipartBody.Part,jobTitle: RequestBody, jobDescription: RequestBody,prevJobId: RequestBody): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.addJob(ServiceBuilder.token!!, prevJobId,image,jobTitle, jobDescription)
        }
    }

    suspend fun updateJobWithoutImage(prevJobId: String,job: Job): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.updateJobWithoutImage(ServiceBuilder.token!!, prevJobId,job)
        }
    }

    suspend fun getJobById(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.getJobById(ServiceBuilder.token!!,jobId)
        }
    }
    suspend fun updateJobDetail(jobId: String, job: Job): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.updateJobDetail(ServiceBuilder.token!!, jobId, job  )
        }
    }

    suspend fun updateJobDirection(jobId: String, job: Job): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.updateJobDirection(ServiceBuilder.token!!, jobId, job  )
        }
    }

    suspend fun updateJobHashtag(jobId: String, job: Job): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.updateJobHashtag(ServiceBuilder.token!!, jobId, job  )
        }
    }

    suspend fun discardJob(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.discardJob(ServiceBuilder.token!!, jobId  )
        }
    }

    suspend fun shareJob(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.shareJob(ServiceBuilder.token!!, jobId  )
        }
    }
    suspend fun postJob(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.postJob(ServiceBuilder.token!!, jobId )
        }
    }
    suspend fun archivedJob(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.archivedJob(ServiceBuilder.token!!, jobId  )
        }
    }

    suspend fun deleteArchived(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.deleteArchived(ServiceBuilder.token!!, jobId  )
        }
    }

    suspend fun viewArchivedJob(): JobsResponse {
        return myHttpRequestNetworkCall {
            jobService.viewArchivedJob(ServiceBuilder.token!!)
        }
    }
    suspend fun reportJob(jobId: String,reportReason:String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.reportJob(ServiceBuilder.token!!, jobId,reportReason)
        }
    }

    suspend fun deleteJob(jobId: String): JobResponse {
        return myHttpRequestNetworkCall {
            jobService.deleteJob(ServiceBuilder.token!!, jobId  )
        }
    }
}