package com.hdd.empowerpro.domain.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.presentation.activity.JobDetailsActivity
import com.hdd.empowerpro.repository.JobRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedJobAdapter(
    private val jobList: MutableList<Job>,
    private val isArchived: Boolean = false
) :
    RecyclerView.Adapter<ProfileJobViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileJobViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.job_recycler_view, parent, false)
        return ProfileJobViewHolder(view)
    }
    override fun onBindViewHolder(holder: ProfileJobViewHolder, position: Int) {
        val job = jobList[position]
        holder.bind(job, isArchived, jobList)

        holder.itemView.setOnLongClickListener {
            val archiveDialog = Dialog(holder.itemView.context)
            archiveDialog.setContentView(R.layout.archive_job_layout)
            archiveDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            val archive: Button = archiveDialog.findViewById(R.id.arl_archive_job)
            val delete: Button = archiveDialog.findViewById(R.id.arl_delete_job)
            // archive
            if (isArchived){
                archive.text="Unarchive"
                archive.setOnClickListener {
                    archivedJob(job._id)
                    jobList.remove(job)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Job Unarchived", Toast.LENGTH_SHORT).show()
                    archiveDialog.dismiss()
                }
            } else {
                archive.text = "Archive"
                archive.setOnClickListener {
                    archivedJob(job._id)
                    jobList.remove(job)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Job archived", Toast.LENGTH_SHORT).show()
                    archiveDialog.dismiss()
                }
            }
            // delete review and rating
            delete.setOnClickListener {
                showDeleteAlertDialog(holder.itemView.context,job._id,job)
                archiveDialog.dismiss()
            }
            archiveDialog.show()
            true
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, JobDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("jobId", job._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }
        private fun showDeleteAlertDialog(context: Context,jobId: String,job: Job) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete this?")
        builder.setMessage("Are you sure want to delete?")
        builder.setIcon(R.drawable.sign_out)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            deleteJob(jobId)
            jobList.remove(job)
            notifyDataSetChanged()
            Toast.makeText(context,"Job Deleted",Toast.LENGTH_SHORT).show()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
    private fun archivedJob(jobId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobRepository = JobRepository()
                jobRepository.archivedJob(jobId)
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }
    private fun deleteJob(jobId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobRepository= JobRepository()
                jobRepository.deleteJob(jobId)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }
}
class ProfileJobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val rcvJobName: TextView = view.findViewById(R.id.rcvJobName)
    private val rcvJobImage: ImageView = view.findViewById(R.id.rcvJobImage)
//        private val archiveSetting: ImageButton = view.findViewById(R.id.archiveSetting)
    fun bind(job: Job, isArchived: Boolean, jobList: MutableList<Job>) {
        Glide.with(itemView.context).load(job.image).into(rcvJobImage)
        rcvJobName.text = job.title

//        archiveSetting.setOnClickListener {
//            loadPopUpSetting(job._id) }
//    }
    }
}