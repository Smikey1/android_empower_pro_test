package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.domain.adapter.FollowersFollowingAdapter
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowersFragment(private val userId: String) : Fragment() {

    private lateinit var followersRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_followers, container, false)
        followersRecyclerView = view.findViewById(R.id.ff_followersRV)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserFollowers()
                if (response.success == true) {
                    val followersList = response.data!!
                    withContext(Dispatchers.Main) {
                        val adapter = FollowersFollowingAdapter(followersList,false)
                        followersRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
                        followersRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
        return view
    }
}
