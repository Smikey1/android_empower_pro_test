package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.domain.adapter.PostAdapter
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PostFragment() : Fragment() {

    private lateinit var fp_profilePostRV: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        fp_profilePostRV = view.findViewById(R.id.fp_profilePostRV)

        getPosts()
        return view
    }

    private fun getPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            val userRepository = UserRepository()
            val response = userRepository.getUserPost()
            if (response.success == true) {
                withContext(Dispatchers.Main) {
                    val adapter = PostAdapter(response.data!!)
                    fp_profilePostRV.layoutManager = LinearLayoutManager(requireActivity())
                    fp_profilePostRV.adapter = adapter
                }
            }
        }
    }
}