package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hdd.empowerpro.R
import com.hdd.empowerpro.domain.adapter.PostAdapter
import com.hdd.empowerpro.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var swipeDownToRefresh: SwipeRefreshLayout
    private lateinit var ff_emptyFollowing: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        swipeDownToRefresh = view.findViewById(R.id.swipeDownToRefresh)
        ff_emptyFollowing = view.findViewById(R.id.ff_emptyFollowing)
        getFollowingPost()
        swipeDownToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getFollowingPost()
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (swipeDownToRefresh.isRefreshing) {
                    swipeDownToRefresh.isRefreshing = false
                }
            }, 2000)
        })
        return view
    }
    private fun getFollowingPost() {
        CoroutineScope(Dispatchers.IO).launch {
            val postRepository = PostRepository()
            val response = postRepository.getFollowingPost()
            if (response.success == true) {
                withContext(Dispatchers.Main) {
                    if (response.data!!.isNotEmpty()){
                        ff_emptyFollowing.visibility=View.GONE
                        val adapter = PostAdapter(response.data)
                        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        postRecyclerView.adapter = adapter
                    } else{
                        ff_emptyFollowing.visibility=View.VISIBLE
                    }
                }
            }
        }
    }

}
