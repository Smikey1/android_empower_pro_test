package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class CreatedJobFragment() : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var swipeDownToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_created_job, container, false)
        postRecyclerView = view.findViewById(R.id.cjf_postRecyclerView)
        swipeDownToRefresh = view.findViewById(R.id.cjf_swipeDownToRefresh)
        getAllPosts()
        swipeDownToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getAllPosts()
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (swipeDownToRefresh.isRefreshing) {
                    swipeDownToRefresh.isRefreshing = false
                }
            }, 2000)
        })
        return view
    }

    private fun getAllPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            val postRepository = PostRepository()
            val response = postRepository.getAllCreatedPost()
            if (response.success == true) {
                withContext(Dispatchers.Main) {
                    val adapter = PostAdapter(response.data!!)
                    postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    postRecyclerView.adapter = adapter
                }
            }
        }
    }
}