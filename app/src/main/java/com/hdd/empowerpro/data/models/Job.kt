package com.hdd.empowerpro.data.models


data class Job(
    var _id:String="",
//    var user: User? = null,
    var title:String? = null,
    var avgRating:Int? = null,
    var description:String? = null,
    var image: String? = null,
    var archive:String? = null,
    var createdDate:String? = null,
    var updatedDate:String? = null,
    var hashtag:MutableList<String>? = null,
    var jobDetailSchema:JobDetailSchema? = null,
    var direction:MutableList<String>? = null,
    var review:MutableList<ReviewRating>?=null
)

data class JobDetailSchema(
    var companyDescription:String? = null,
    var responsibilities:String? = null,
    var eligibilityCriteria:String? = null,
    var skills: String? = null
)