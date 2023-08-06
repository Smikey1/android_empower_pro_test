package com.hdd.empowerpro.data.models


data class Job(
    var _id:String="",
    var title:String? = null,
    var description:String? = null,
    var archive:String? = null,
    var createdDate:String? = null,
    var updatedDate:String? = null,
    var hashtag:MutableList<String>? = null,
)