package com.hdd.empowerpro.domain.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.presentation.activity.ViewOtherProfileActivity
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowersFollowingAdapter(private val userList: MutableList<User>, private val isFromFollowing:Boolean=false) :
    RecyclerView.Adapter<FollowingViewHolder>() {
    private var alreadyFollowed: Boolean=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.user_item_layout, parent, false)
        return FollowingViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(holder.itemView.context).load(user.profile).circleCrop().into(holder.uil_Profile)
        holder.uil_fullname.text = user.fullname
        holder.uil_bio.text = user.bio
        holder.uil_btn_following.text = "Follow"
        holder.uil_Profile.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewOtherProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userId", user._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(holder.itemView.context,intent,null)
        }
        if (isFromFollowing) {
            alreadyFollowed = true
            holder.uil_btn_following.text = "Following"
        }
        if (ServiceBuilder.followingList.contains(user._id)) {
            holder.uil_btn_following.text = "Following"
        }
        holder.uil_btn_following.setOnClickListener {
            if (alreadyFollowed) {
                updateFollowing(user._id, false)
                holder.uil_btn_following.text = "Follow"
            } else {
                updateFollowing(user._id, true)
                holder.uil_btn_following.text = "Following"
            }
        }
    }

    private fun updateFollowing(userId: String, followedAlready: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository= UserRepository()
                val response=userRepository.followUser(userId)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        alreadyFollowed=followedAlready
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }

}

class FollowingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val uil_Profile: ImageView = view.findViewById(R.id.uil_Profile)
    val uil_fullname: TextView = view.findViewById(R.id.uil_fullname)
    val uil_bio: TextView = view.findViewById(R.id.uil_bio)
    val uil_btn_following: Button = view.findViewById(R.id.uil_btn_follow)
}