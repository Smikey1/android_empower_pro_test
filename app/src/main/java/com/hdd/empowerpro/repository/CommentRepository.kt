package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.Comment
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.CommentResponse
import com.hdd.empowerpro.data.remoteDataSource.response.CommentsResponse
import com.hdd.empowerpro.data.remoteDataSource.services.CommentServices

class CommentRepository : HttpRequestNetworkCall() {
    private val commentService = ServiceBuilder.buildService(CommentServices::class.java)

    suspend fun getAllComment(postId:String): CommentsResponse {
        return myHttpRequestNetworkCall {
            commentService.getAllComment(postId)
        }
    }

    suspend fun deleteComment(postId:String): CommentResponse {
        return myHttpRequestNetworkCall {
            commentService.deleteComment(ServiceBuilder.token!!, postId)
        }
    }

    suspend fun addComment(postId:String,comment: Comment): CommentsResponse {
        return myHttpRequestNetworkCall {
            commentService.addComment(ServiceBuilder.token!!,postId,comment)
        }
    }

    suspend fun editComment(commentId:String,comment: Comment): CommentResponse {
        return myHttpRequestNetworkCall {
            commentService.editComment(ServiceBuilder.token!!,commentId,comment)
        }
    }
}