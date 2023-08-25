package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.domain.adapter.SavedJobAdapter
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SavedJobFragment() : Fragment() {
    private lateinit var fs_profileRecipeRV: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_saved_job, container, false)
        fs_profileRecipeRV = view.findViewById(R.id.fs_profileJobRV)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getAllUserSavedJob()
                if (response.success == true) {
                    val jobList = response.data!!
                    withContext(Dispatchers.Main) {
                        val adapter = SavedJobAdapter(jobList)
                        fs_profileRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
                        fs_profileRecipeRV.adapter = adapter
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