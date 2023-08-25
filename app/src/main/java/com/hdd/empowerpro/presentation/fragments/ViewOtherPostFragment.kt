package com.hdd.pakwan.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.domain.adapter.PostAdapter


class ViewOtherPostFragment(private val user: User) : Fragment() {

    private lateinit var fpvo_profilePostRV:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_view_other, container, false)
        fpvo_profilePostRV = view.findViewById(R.id.fpvo_profilePostRV)
        val adapter = PostAdapter(user.post!!)
        fpvo_profilePostRV.layoutManager = LinearLayoutManager(requireActivity())
        fpvo_profilePostRV.adapter = adapter
        return view
    }
}