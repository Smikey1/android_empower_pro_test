package com.hdd.pakwan.presentation.fragments

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

class FollowingFragment(private val userId: String) : Fragment() {

    private lateinit var followingRecyclerView: RecyclerView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_following, container, false)
        followingRecyclerView = view.findViewById(R.id.ff_followingRV)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserFollowing()
                if (response.success == true) {
                    val followingList = response.data!!
                    withContext(Dispatchers.Main) {
                        val adapter = FollowersFollowingAdapter(followingList,true)
                        followingRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
                        followingRecyclerView.adapter = adapter
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