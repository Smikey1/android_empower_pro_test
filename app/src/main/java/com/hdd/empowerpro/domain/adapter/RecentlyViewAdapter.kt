package com.hdd.empowerpro.domain.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.presentation.activity.JobDetailsActivity

class RecentlyViewAdapter(private val jobList: MutableList<Job>) :
    RecyclerView.Adapter<RecentlyViewedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyViewedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.job_recycler_view, parent, false)
        return RecentlyViewedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentlyViewedViewHolder, position: Int) {
        val job = jobList[position]
        holder.bind(job)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, JobDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("jobId", job._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }
}

class RecentlyViewedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val rcvJobName: TextView = view.findViewById(R.id.rcvJobName)
    private val rcvJobImage: ImageView = view.findViewById(R.id.rcvJobImage)
    fun bind(job: Job) {
        Glide.with(itemView.context).load(job.image).into(rcvJobImage)
        rcvJobName.text = job.title
    }
}