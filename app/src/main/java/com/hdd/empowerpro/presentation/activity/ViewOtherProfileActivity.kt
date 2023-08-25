package com.hdd.empowerpro.presentation.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.ViewPager2Adapter
import com.hdd.empowerpro.repository.UserRepository
import com.hdd.empowerpro.presentation.fragments.ViewOtherJobFragment
import com.hdd.pakwan.presentation.fragments.ViewOtherPostFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewOtherProfileActivity : AppCompatActivity() {

    private var isFollowedAlready: Boolean = false
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var ap_profile_viewPager: ViewPager2
    private lateinit var ap_profile_tabLayout: TabLayout
    private lateinit var ap_profile_image: ImageView
    private lateinit var ap_profile_name: TextView
    private lateinit var ap_postCounter: TextView
    private lateinit var ap_jobCounter: TextView
    private lateinit var ap_profile_description: TextView
    private lateinit var ap_profile_website: TextView
    private lateinit var ap_profile_following: TextView
    private lateinit var ap_profile_followers: TextView
    private lateinit var btnFollowing: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_other_profile)

        // for profile layout
        ap_profile_viewPager = findViewById(R.id.ap_profile_viewPager)
        ap_profile_tabLayout = findViewById(R.id.ap_profile_tabLayout)
        ap_profile_image = findViewById(R.id.ap_profile_image)
        ap_profile_name = findViewById(R.id.ap_profile_name)
        ap_postCounter = findViewById(R.id.ap_postCounter)
        ap_jobCounter = findViewById(R.id.ap_jobCounter)
        ap_profile_website = findViewById(R.id.ap_profile_website)
        ap_profile_description = findViewById(R.id.ap_profile_description)
        btnFollowing = findViewById(R.id.ap_btn_following)
        ap_profile_following = findViewById(R.id.ap_profile_following)
        ap_profile_followers = findViewById(R.id.ap_profile_followers)

        //Extract the data…
        val bundle = intent.extras
        val requiredUserId = bundle!!.getString("userId")!!
        fetchData(requiredUserId)
        if(ServiceBuilder.user!!._id == requiredUserId){
            btnFollowing.visibility = View.INVISIBLE
        }
        else{
            btnFollowing.setOnClickListener {
                    checkFollow(requiredUserId)
            }
        }
    }

    private fun checkFollow(requiredUserId: String) {
        if (isFollowedAlready) {
            updateFollowing(requiredUserId,false)
            btnFollowing.text = "Follow"
        } else {
            updateFollowing(requiredUserId,true)
            btnFollowing.text = "Following"
        }
    }

    private fun fetchData(id:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getOtherUserProfile(id)
                if (response.success == true) {
                    val user = response.data!!
                    withContext(Dispatchers.Main) {
                        if(user.bio == ""){
                            ap_profile_description.visibility = View.GONE
                        }
                        if(user.website == ""){
                            ap_profile_website.visibility = View.GONE
                        }
                        ap_profile_following.text = "${user.following!!.size} Followings"
                        ap_profile_followers.text = "${user.follower!!.size} Followers"
                        ap_profile_name.text = user.fullname
                        ap_profile_description.text = user.bio
                        ap_postCounter.text=user.post!!.size.toString()
                        ap_jobCounter.text=user.job!!.size.toString()
                        isFollowedAlready = user.isFollowed!!
                        setFollowing()
                        Glide.with(this@ViewOtherProfileActivity).load(user.profile).circleCrop().into(ap_profile_image)
                        tabTitleList = arrayListOf<String>("Post", "Job")
                        fragmentList = arrayListOf<Fragment>(
                            ViewOtherPostFragment(user),
                            ViewOtherJobFragment(user),
                        )

                        // setting up adapter class or view pager2
                        val adapter = ViewPager2Adapter(fragmentList, supportFragmentManager, lifecycle)
                        ap_profile_viewPager.adapter = adapter
                        TabLayoutMediator(ap_profile_tabLayout, ap_profile_viewPager) {
                                tab, position -> tab.text = tabTitleList[position]
                        }.attach()

                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun setFollowing() {
        if (isFollowedAlready) {
            btnFollowing.text = "Following"
        } else {
            btnFollowing.text = "Follow"
        }
    }

    private fun updateFollowing(userId: String, followedAlready: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository= UserRepository()
                val response=userRepository.followUser(userId)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        isFollowedAlready=followedAlready
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

}