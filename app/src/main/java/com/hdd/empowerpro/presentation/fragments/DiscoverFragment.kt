package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R


class DiscoverFragment() : Fragment() {
    private lateinit var svSearch: SearchView
    private lateinit var searchRecipeRecyclerView: RecyclerView
    private lateinit var searchUserRecyclerView: RecyclerView
    private lateinit var showOnEmptySearch: ImageView
    private lateinit var tvSearchedRecipe: TextView
    private lateinit var tvSearchedUser: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        svSearch = view.findViewById(R.id.svSearch)
        searchRecipeRecyclerView = view.findViewById(R.id.searchRecipeRecyclerView)
        searchUserRecyclerView = view.findViewById(R.id.searchUserRecyclerView)
        showOnEmptySearch = view.findViewById(R.id.showOnEmptySearch)
        tvSearchedRecipe = view.findViewById(R.id.tvSearchedRecipe)
        tvSearchedUser = view.findViewById(R.id.tvSearchedUser)
        svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(query: String?): Boolean {
                    if (query!!.trim().isNotEmpty()) {
//                        search(Search(pattern = query))
                        return true
                    }
                    return false
                }
            }
        )
        return view
    }


}