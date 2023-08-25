package com.hdd.empowerpro.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var _id: String = "",

    @ColumnInfo(name = "user_fullname")
    var fullname: String? = null,

    @ColumnInfo(name = "user_username")
    var username: String? = null,

    @ColumnInfo(name = "user_email")
    var email: String? = null,

    @ColumnInfo(name = "user_address")
    var address: String? = null,

    @ColumnInfo(name = "user_country_code")
    var countryCode: String? = null,

    @ColumnInfo(name = "user_phone")
    var phone: String? = null,

    @ColumnInfo(name = "user_old_password")
    val oldPassword: String? = null,

    @ColumnInfo(name = "user_password")
    var password: String? = null,

    @ColumnInfo(name = "user_new_password")
    var newPassword: String? = null,

    @ColumnInfo(name = "user_confirm_password")
    var confirmPassword: String? = null,

    @ColumnInfo(name = "user_bio")
    var bio: String? = null,

    @ColumnInfo(name = "user_follow")
    var isFollowed: Boolean? = null,

    @ColumnInfo(name = "user_website")
    var website: String? = null,

    @ColumnInfo(name = "user_profile_pic")
    var profile: String? = null,

    @ColumnInfo(name = "user_profile_post")
    var post: MutableList<Post>? = null,

    @ColumnInfo(name = "user_profile_job")
    var job: MutableList<Job>? = null,

    @ColumnInfo(name = "user_follower")
    var follower: MutableList<String>? = null,

    @ColumnInfo(name = "user_following")
    var following: MutableList<String>? = null,

    @ColumnInfo(name = "user_saved_job")
    var savedJob: MutableList<Job>? = null,

    @ColumnInfo(name = "user_saved_job")
    var appliedJob: MutableList<Job>? = null,

    @ColumnInfo(name = "user_recently_viewed_job")
    var recentlyViewed: MutableList<Job>? = null,

    @ColumnInfo(name = "user_restaurant")
    var company: Company?= null,

    @ColumnInfo(name = "user_reset_code")
    var resetCode: String? = null,

    @ColumnInfo(name = "user_2f_login_code")
    var loginCode: String? = null,

    @ColumnInfo(name = "user_device_name")
    var androidId: String? = null,
)