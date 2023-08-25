package com.hdd.empowerpro.domain.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.presentation.activity.ViewOtherProfileActivity

class SearchUserAdapter(private val searchUserList: MutableList<User>) :
    RecyclerView.Adapter<SearchUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_user_layout, parent, false)
        return SearchUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val searchUser = searchUserList[position]
        holder.bind(searchUser)
    }

    override fun getItemCount(): Int {
        return searchUserList.size
    }
}

class SearchUserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val sul_tv_fullname: TextView = view.findViewById(R.id.sul_tv_fullname)
    private val sul_profile_image: ImageView = view.findViewById(R.id.sul_profile_image)
    fun bind(searchUser: User) {
        Glide.with(itemView.context).load(searchUser?.profile).circleCrop().into(sul_profile_image)
        sul_tv_fullname.text = searchUser?.fullname
        itemView.setOnClickListener {
            val intent = Intent(itemView.context, ViewOtherProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userId", searchUser!!._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(itemView.context, intent, null)
        }
    }
}