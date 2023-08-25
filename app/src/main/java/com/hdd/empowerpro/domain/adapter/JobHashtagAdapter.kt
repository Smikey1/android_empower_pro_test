package com.hdd.empowerpro.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R

class JobHashtagAdapter(private val jobHashtagList: MutableList<String>, private val isViewing:Boolean=false): RecyclerView.Adapter<RecipeHashtagViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHashtagViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.job_hashtags_items,parent,false)
        return RecipeHashtagViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecipeHashtagViewHolder, position: Int) {
        val hashtag = jobHashtagList[position]
        holder.rdi_stepName.text= hashtag

        if (isViewing){
            holder.rdi_stepCancel.visibility=View.GONE
        }

        holder.rdi_stepCancel.setOnClickListener{
            jobHashtagList.remove(hashtag)
            Toast.makeText(it.context, "Item removed", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }

    }
    override fun getItemCount(): Int {
        return jobHashtagList.size
    }
}
class RecipeHashtagViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val rdi_stepName: TextView =view.findViewById(R.id.rdi_stepName)
    val rdi_stepCancel: ImageButton =view.findViewById(R.id.rdi_stepCancel)
}