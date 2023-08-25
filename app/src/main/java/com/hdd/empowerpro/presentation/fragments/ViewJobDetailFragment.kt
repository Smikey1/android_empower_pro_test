package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.JobDetailSchema

class ViewJobDetailFragment(private val jobDetailSchema: JobDetailSchema) : Fragment() {

    private lateinit var companyDescription: TextView
    private lateinit var responsibilities: TextView
    private lateinit var eligibilityCriteria: TextView
    private lateinit var skills: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_job_detail, container, false)
        companyDescription = view.findViewById(R.id.companyDescription)
        responsibilities = view.findViewById(R.id.responsibilities)
        eligibilityCriteria = view.findViewById(R.id.eligibilityCriteria)
        skills = view.findViewById(R.id.skills)

        setPreparationDetails(jobDetailSchema)
        return view
    }

    private fun setPreparationDetails(jobDetailSchema:JobDetailSchema) {
        companyDescription.text = jobDetailSchema.companyDescription.toString()
        responsibilities.text = jobDetailSchema.responsibilities.toString()
        eligibilityCriteria.text = jobDetailSchema.eligibilityCriteria.toString()
        skills.text = jobDetailSchema.skills.toString()
    }
}