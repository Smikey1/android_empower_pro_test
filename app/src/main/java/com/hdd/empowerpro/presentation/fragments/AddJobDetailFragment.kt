package com.hdd.empowerpro.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.data.models.JobDetailSchema
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.JobRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddJobDetailFragment(private val isEditMode:Boolean=false, private val jobDetailSchema: JobDetailSchema = JobDetailSchema()) : Fragment() {
    private lateinit var companyDescription:TextView
    private lateinit var responsibilities:TextView
    private lateinit var eligibilityCriteria:TextView
    private lateinit var skills:TextView
    private lateinit var job_empty_details:TextView
    private lateinit var btnAddJobDetails:Button
    private lateinit var jobDetailLayout:LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var isNotFilled = true
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_job_detail, container, false)
        companyDescription= view.findViewById(R.id.companyDescription)
        responsibilities= view.findViewById(R.id.responsibilities)
        eligibilityCriteria= view.findViewById(R.id.eligibilityCriteria)
        skills= view.findViewById(R.id.skills)
        job_empty_details= view.findViewById(R.id.empty_job_details)
        btnAddJobDetails= view.findViewById(R.id.btnAddJobDetails)
        jobDetailLayout= view.findViewById(R.id.jobDetailLayout)
        jobDetailLayout.visibility = View.GONE

        // in case of update or edit
        if (isEditMode){
            companyDescription.text=jobDetailSchema.companyDescription
            responsibilities.text=jobDetailSchema.responsibilities!!
            eligibilityCriteria.text= jobDetailSchema.eligibilityCriteria
            skills.text=jobDetailSchema.skills
            isNotFilled=false
            jobDetailLayout.visibility = View.VISIBLE
            job_empty_details.visibility = View.GONE
            btnAddJobDetails.text = "Edit Details"
        }

        btnAddJobDetails.setOnClickListener {
            if (isNotFilled) {
                val jobDetailDialog = Dialog(requireContext())
                jobDetailDialog.setContentView(R.layout.add_job_detail_dialog)
                jobDetailDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                val etAddCompanyDescription: EditText = jobDetailDialog.findViewById(R.id.etAddCompanyDescription)
                val etAddResponsibilities: EditText = jobDetailDialog.findViewById(R.id.etAddResponsibilities)
                val etAddEligibilityCriteria: EditText = jobDetailDialog.findViewById(R.id.etAddEligibilityCriteria)
                val etAddSkill: EditText = jobDetailDialog.findViewById(R.id.etAddSkill)
                val cancel: Button = jobDetailDialog.findViewById(R.id.apd_cancel)
                val ok: Button = jobDetailDialog.findViewById(R.id.apd_ok)
                
                cancel.setOnClickListener {
                    jobDetailDialog.dismiss()
                    job_empty_details.visibility = View.VISIBLE
                    btnAddJobDetails.text = "Add Details"
                }
                
                ok.setOnClickListener {
                    if (
                        etAddResponsibilities.text.isNotEmpty() && etAddCompanyDescription.text.isNotEmpty() && etAddEligibilityCriteria.text.isNotEmpty() && etAddSkill.text.isNotEmpty()
                    ){
                        companyDescription.text = etAddCompanyDescription.text.toString()
                        responsibilities.text = etAddResponsibilities.text.toString()
                        eligibilityCriteria.text = etAddEligibilityCriteria.text.toString()
                        skills.text = etAddSkill.text.toString()
                        isNotFilled = false
                        jobDetailLayout.visibility = View.VISIBLE
                        job_empty_details.visibility = View.GONE
                        updateRecipePreparation(
                            ServiceBuilder.jobId,
                            etAddCompanyDescription.text.toString(),
                            etAddResponsibilities.text.toString(),
                            etAddEligibilityCriteria.text.toString(),
                            etAddSkill.text.toString(),
                        )
                        btnAddJobDetails.text = "Edit Details"
                        jobDetailDialog.dismiss()
                    } else{
                        etAddCompanyDescription.error="This is required"
                        etAddResponsibilities.error="This is required"
                        etAddEligibilityCriteria.error="This is required"
                        etAddSkill.error="This is required"
                    }
                }
                jobDetailDialog.show()
            }
            else {
                job_empty_details.visibility = View.GONE
                btnAddJobDetails.setOnClickListener {
                    val jobDetailDialog = Dialog(requireContext())
                    jobDetailDialog.setContentView(R.layout.update_job_detail_dialog)
                    jobDetailDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    val etAddCompanyDescription: EditText = jobDetailDialog.findViewById(R.id.etAddCompanyDescription)
                    val etAddResponsibilities: EditText = jobDetailDialog.findViewById(R.id.etAddResponsibilities)
                    val etAddEligibilityCriteria: EditText = jobDetailDialog.findViewById(R.id.etAddEligibilityCriteria)
                    val etAddSkill: EditText = jobDetailDialog.findViewById(R.id.etAddSkill)

                    val cancel: Button = jobDetailDialog.findViewById(R.id.upd_cancel)
                    val btnUpdate: Button = jobDetailDialog.findViewById(R.id.upd_update)

                    etAddCompanyDescription.setText(companyDescription.text)
                    etAddResponsibilities.setText(companyDescription.text)
                    etAddEligibilityCriteria.setText(eligibilityCriteria.text)
                    etAddSkill.setText(skills.text)

                    cancel.setOnClickListener {
                        jobDetailDialog.dismiss()
                    }

                    btnUpdate.setOnClickListener {
                        if (etAddResponsibilities.text.isNotEmpty() && etAddCompanyDescription.text.isNotEmpty() && etAddEligibilityCriteria.text.isNotEmpty() && etAddSkill.text.isNotEmpty()){
                            jobDetailLayout.visibility = View.VISIBLE
                            companyDescription.text = etAddCompanyDescription.text.toString()
                            responsibilities.text = etAddResponsibilities.text.toString()
                            eligibilityCriteria.text = etAddEligibilityCriteria.text.toString()
                            skills.text = etAddSkill.text.toString()
                            isNotFilled = false
                            updateRecipePreparation(
                                ServiceBuilder.jobId,
                                etAddCompanyDescription.text.toString(),
                                etAddResponsibilities.text.toString(),
                                etAddEligibilityCriteria.text.toString(),
                                etAddSkill.text.toString(),
                            )
                            jobDetailLayout.visibility = View.VISIBLE
                            job_empty_details.visibility = View.GONE
                            btnAddJobDetails.text = "Edit Details"
                            jobDetailDialog.dismiss()
                        } else {
                            etAddCompanyDescription.error="This is required"
                            etAddResponsibilities.error="This is required"
                            etAddEligibilityCriteria.error="This is required"
                            etAddSkill.error="This is required"
                        }

                    }
                    jobDetailDialog.show()
                }
            }
        }
        return view
    }

    private fun updateRecipePreparation(jobId: String?, companyDescription: String, responsibilities: String, eligibilityCriteria: String, skills: String) {
        val jobDetailSchema = JobDetailSchema(companyDescription, responsibilities, eligibilityCriteria, skills)
        val job = Job(jobDetailSchema = jobDetailSchema)
        try {
            val jobRepository = JobRepository()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    jobRepository.updateJobDetail(jobId!!,job)
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

}