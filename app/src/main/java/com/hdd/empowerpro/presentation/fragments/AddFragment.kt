package com.hdd.empowerpro.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.hdd.empowerpro.R
import com.hdd.empowerpro.presentation.activity.AddCompanyActivity
import com.hdd.empowerpro.presentation.activity.AddJobActivity
import com.hdd.empowerpro.presentation.activity.AddPostActivity


class AddFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        val addPost: ImageView = view.findViewById(R.id.fa_CreatePost)
        val addJob: ImageView = view.findViewById(R.id.fa_CreateJob)
        val addCompany: ImageView = view.findViewById(R.id.fa_AddCompany)

        addPost.setOnClickListener {
            startActivity(Intent(requireActivity(), AddPostActivity::class.java))
        }

        addJob.setOnClickListener {
            startActivity(Intent(requireActivity(), AddJobActivity::class.java))
        }

        addCompany.setOnClickListener {
            startActivity(Intent(requireActivity(), AddCompanyActivity::class.java))
        }
        return view
    }
}