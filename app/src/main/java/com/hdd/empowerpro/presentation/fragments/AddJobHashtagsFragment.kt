package com.hdd.empowerpro.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.JobHashtagAdapter
import com.hdd.empowerpro.repository.JobRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class AddJobHashtagsFragment(private val isEditMode:Boolean=false, private val hashtagList: MutableList<String> =mutableListOf()) : Fragment() {
    private var isDone: Boolean = false
    private lateinit var adapter : JobHashtagAdapter
    private lateinit var jobHashtagList : MutableList<String>

    private lateinit var fdr_btnAddStep : Button
    private lateinit var fdr_etAddStepName : EditText
    private lateinit var fdr_recyclerView : RecyclerView
    private lateinit var fdr_btnDone : Button
    private lateinit var snackBarFDRLinearLayout : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_job_hashtags, container, false)
        fdr_btnAddStep=view.findViewById(R.id.fdr_btnAddStep)
        fdr_etAddStepName=view.findViewById(R.id.fdr_etAddStepName)
        fdr_recyclerView=view.findViewById(R.id.fdr_recyclerView)
        fdr_btnDone=view.findViewById(R.id.fdr_btnDone)
        snackBarFDRLinearLayout=view.findViewById(R.id.snackBarFDRLinearLayout)

        jobHashtagList = mutableListOf<String>()

        val linearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }

        if (isEditMode){
            jobHashtagList=hashtagList
            adapter = JobHashtagAdapter(jobHashtagList)
            fdr_recyclerView.layoutManager = linearLayoutManager
            fdr_recyclerView.adapter=adapter
            adapter.notifyDataSetChanged()
            fdr_btnDone.visibility=View.INVISIBLE
            isDone=true
        }

        adapter = JobHashtagAdapter(jobHashtagList)
        fdr_btnAddStep.setOnClickListener {
            if (isDone) {
                if (fdr_etAddStepName.text.isNotEmpty()){
                    fdr_btnDone.visibility=View.VISIBLE
                    jobHashtagList.add(fdr_etAddStepName.text.toString())
                    adapter.notifyDataSetChanged()
                    fdr_etAddStepName.setText("")
                } else {
                    fdr_etAddStepName.error = "This is required"
                }
            }   else {
                if (fdr_etAddStepName.text.isNotEmpty()) {
                    fdr_btnDone.visibility=View.VISIBLE
                    jobHashtagList.add(fdr_etAddStepName.text.toString())
                    adapter.notifyDataSetChanged()
                    fdr_etAddStepName.setText("")
                } else {
                    fdr_etAddStepName.error = "This is required"
                }
            }

            fdr_recyclerView.layoutManager = linearLayoutManager
            fdr_recyclerView.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(fdr_recyclerView)
            fdr_btnDone.setOnClickListener {
                updateJobHashtag(ServiceBuilder.jobId, jobHashtagList)
                isDone = true
                fdr_btnAddStep.text = "Edit Details"
                fdr_btnDone.visibility = View.GONE
            }
        }
        return view
    }

    private val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0) {
        override fun onMove(recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean {
            when {
                isDone -> {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition =target.adapterPosition
                    Collections.swap(jobHashtagList,fromPosition,toPosition)
                    fdr_btnDone.visibility=View.VISIBLE
                    recyclerView.adapter!!.notifyItemMoved(fromPosition,toPosition)
                    return false
                }
                else -> {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition =target.adapterPosition
                    Collections.swap(jobHashtagList,fromPosition,toPosition)
                    recyclerView.adapter!!.notifyItemMoved(fromPosition,toPosition)
                    return false
                }
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, hashtag: Int) {

        }

    }
    private fun updateJobHashtag(recipeId: String?, jobHashtagList: MutableList<String>) {
        val recipe = Job(hashtag = jobHashtagList)
        try {
            val recipeRepository = JobRepository()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    recipeRepository.updateJobHashtag(recipeId!!,recipe)
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}