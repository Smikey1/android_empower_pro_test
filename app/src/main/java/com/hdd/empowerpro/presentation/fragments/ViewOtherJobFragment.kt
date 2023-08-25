package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.domain.adapter.SavedJobAdapter


class ViewOtherJobFragment(private val user: User) : Fragment() {

    private lateinit var frvo_profileJobRV:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_job_view_other, container, false)
        frvo_profileJobRV = view.findViewById(R.id.frvo_profileJobRV)
        val adapter = SavedJobAdapter(user.job!!)
        frvo_profileJobRV.layoutManager = LinearLayoutManager(requireActivity())
        frvo_profileJobRV.adapter = adapter
        return view
    }
}