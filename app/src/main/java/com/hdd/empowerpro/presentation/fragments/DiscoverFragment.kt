package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Search
import com.hdd.empowerpro.domain.adapter.SavedJobAdapter
import com.hdd.empowerpro.domain.adapter.SearchUserAdapter
import com.hdd.empowerpro.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DiscoverFragment() : Fragment() {
    private lateinit var svSearch: android.widget.SearchView
    private lateinit var searchJobRecyclerView: RecyclerView
    private lateinit var searchUserRecyclerView: RecyclerView
    private lateinit var showOnEmptySearch: ImageView
    private lateinit var tvSearchedJob: TextView
    private lateinit var tvSearchedUser: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        svSearch = view.findViewById(R.id.svSearch)
        searchJobRecyclerView = view.findViewById(R.id.searchJobRecyclerView)
        searchUserRecyclerView = view.findViewById(R.id.searchUserRecyclerView)
        showOnEmptySearch = view.findViewById(R.id.showOnEmptySearch)
        tvSearchedJob = view.findViewById(R.id.tvSearchedJob)
        tvSearchedUser = view.findViewById(R.id.tvSearchedUser)
        svSearch.setOnQueryTextListener(
            object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(query: String?): Boolean {
                    if (query!!.trim().isNotEmpty()) {
                        search(Search(pattern = query))
                        return true
                    }
                    return false
                }
            }
        )
        return view
    }

    private fun search(pattern: Search) {
        try {
            val searchRepository = SearchRepository()
            CoroutineScope(Dispatchers.IO).launch {
                val response = searchRepository.search(pattern)
                withContext(Dispatchers.Main) {
                    val user = response.data!!.user!!
                    val job = response.data!!.job!!
                    if (user.size == 0 && job.size == 0) {
                        showOnEmptySearch.visibility = View.VISIBLE
                    } else {
                        tvSearchedJob.visibility = View.VISIBLE
                        tvSearchedUser.visibility = View.VISIBLE
                        if (user.size == 0) {
                            tvSearchedUser.visibility = View.GONE
                        }
                        if (job.size == 0) {
                            tvSearchedJob.visibility = View.GONE
                        }
                        showOnEmptySearch.visibility = View.GONE

                        val searchUserAdapter = SearchUserAdapter(user)
                        val gridRecipeAdapter = SavedJobAdapter(job)
                        searchUserRecyclerView.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        searchJobRecyclerView.layoutManager =
                            GridLayoutManager(requireContext(), 2)
                        searchUserRecyclerView.adapter = searchUserAdapter
                        searchJobRecyclerView.adapter = gridRecipeAdapter
                    }
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}