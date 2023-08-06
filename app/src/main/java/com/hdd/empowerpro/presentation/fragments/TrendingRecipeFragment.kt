package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hdd.empowerpro.R


class TrendingRecipeFragment() : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var swipeDownToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trending_recipe, container, false)
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        swipeDownToRefresh = view.findViewById(R.id.swipeDownToRefresh)
//        getPosts()
        swipeDownToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
//            getPosts()
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (swipeDownToRefresh.isRefreshing) {
                    swipeDownToRefresh.isRefreshing = false
                }
            }, 2000)
        })
        return view
    }
//    private fun getPosts() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val postRepository = PostRepository()
//            val response = postRepository.getTrendingPost()
//            if (response.success == true) {
//                withContext(Dispatchers.Main) {
//                    val adapter = PostAdapter(response.data!!)
//                    postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//                    postRecyclerView.adapter = adapter
//                }
//            }
//        }
//    }
}