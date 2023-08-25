package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.*
import com.hdd.empowerpro.data.remoteDataSource.services.UserServices
import okhttp3.MultipartBody


class UserRepository : HttpRequestNetworkCall() {
    private val userService = ServiceBuilder.buildService(UserServices::class.java)

    suspend fun registerUser(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.registerUser(user)
        }
    }
    suspend fun loginUser(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.login(user)
        }
    }
    suspend fun getUserProfile(): UserResponse {
        return myHttpRequestNetworkCall {
            userService.getUserProfile(ServiceBuilder.token!!)
        }
    }

    suspend fun savedJob(jobId:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.getSingleSavedJob(ServiceBuilder.token!!,jobId)
        }
    }
    suspend fun applyJob(jobId:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.applyJob(ServiceBuilder.token!!,jobId)
        }
    }
    suspend fun getAllUserSavedJob(): JobsResponse {
        return myHttpRequestNetworkCall {
            userService.getAllUserSavedJob(ServiceBuilder.token!!)
        }
    }
    suspend fun getAllUserAppliedJob(): JobsResponse {
        return myHttpRequestNetworkCall {
            userService.getAllUserAppliedJob(ServiceBuilder.token!!)
        }
    }

    suspend fun getUserPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            userService.getUserPost(ServiceBuilder.token!!)
        }
    }

    suspend fun getOtherUserProfile(id:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.getOtherUserProfile(ServiceBuilder.token!!,id)
        }
    }

    suspend fun resendLoginOTP(): UserResponse {
        return myHttpRequestNetworkCall {
            userService.resendLoginOTP(ServiceBuilder.token!!)
        }
    }

    suspend fun updateUserProfile(user: User)
            : UserResponse {
        return myHttpRequestNetworkCall {
            userService.updateUserProfile(ServiceBuilder.token!!,user)
        }
    }

    suspend fun followUser(id:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.followUser(ServiceBuilder.token!!,id)
        }
    }

    suspend fun getUserFollowers(): FollowersFollowingResponse {
        return myHttpRequestNetworkCall {
            userService.getUserFollowers(ServiceBuilder.token!!)
        }
    }
    suspend fun getUserFollowing(): FollowersFollowingResponse {
        return myHttpRequestNetworkCall {
            userService.getUserFollowing(ServiceBuilder.token!!)
        }
    }

    suspend fun updateUserEmailOrPhone(user: User)
            : UserResponse {
        return myHttpRequestNetworkCall {
            userService.updateUserEmailOrPhone(ServiceBuilder.token!!,user)
        }
    }

    suspend fun updateUserDevice(user: User)
            : UserResponse {
        return myHttpRequestNetworkCall {
            userService.updateUserDevice(ServiceBuilder.token!!,user)
        }
    }

    suspend fun uploadImage(body: MultipartBody.Part)
            : ImageResponse {
        return myHttpRequestNetworkCall {
            userService.uploadImage(ServiceBuilder.token!!, body)
        }
    }

    suspend fun changePassword(user: User)
            : UserResponse {
        return myHttpRequestNetworkCall {
            userService.changePassword(ServiceBuilder.token!!, user)
        }
    }

    suspend fun resetUserCodeForResetPassword(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.resetUserCodeForResetPassword(user)
        }
    }

    suspend fun resetUserCodeForUpdateEmailPhone(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.resetUserCodeForUpdateEmailPhone(ServiceBuilder.token!!,user)
        }
    }

    suspend fun setNewPassword(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.setNewPassword(user)
        }
    }
    suspend fun validateEmail(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.validateEmail(user)
        }
    }


}