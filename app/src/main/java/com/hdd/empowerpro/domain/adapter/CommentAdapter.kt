package com.hdd.empowerpro.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Comment
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.CommentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentAdapter(private val commentList: MutableList<Comment>) :
    RecyclerView.Adapter<CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.comment_item_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        Glide.with(holder.itemView.context).load(comment.user!!.profile).circleCrop()
            .into(holder.cil_Profile)
        holder.cil_fullname.text = comment.user.fullname
        holder.cil_comment.text = comment.comment
        if (comment.user._id == ServiceBuilder.user!!._id) {
            holder.cil_layout.visibility = View.VISIBLE
        }
        holder.cil_edit_comment.setOnClickListener {
            holder.cil_input_edit_comment.visibility = View.VISIBLE
            holder.cil_btn_update_comment.visibility = View.VISIBLE
            holder.cil_comment.visibility = View.GONE
            holder.cil_input_edit_comment.setText(holder.cil_comment.text)
        }
        holder.cil_delete_comment.setOnClickListener {
            showDeleteAlertDialog(holder.itemView.context, comment, holder.cil_layout)
        }
        holder.cil_btn_update_comment.setOnClickListener {
            if (holder.cil_input_edit_comment.text.isNotEmpty()) {
                val newInputComment = holder.cil_input_edit_comment.text.toString()
                editComment(comment._id, newInputComment)
                holder.cil_comment.visibility = View.VISIBLE
                holder.cil_comment.text = newInputComment
                holder.cil_input_edit_comment.visibility = View.GONE
                holder.cil_btn_update_comment.visibility = View.GONE

            } else {
                holder.cil_input_edit_comment.error = "This is required"
            }
        }
        //TODO #5: On Long Click Listener
    }

    private fun editComment(commentId: String, newComment: String) {
        CoroutineScope(Dispatchers.IO).launch {
            CommentRepository().editComment(commentId, Comment(comment = newComment))
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    private fun showDeleteAlertDialog(context: Context, comment: Comment, cil_layout: LinearLayout) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete comment")
        builder.setMessage("Are you sure want to delete this comment?")
        builder.setIcon(R.drawable.cross)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            commentList.remove(comment)
            notifyDataSetChanged()
            cil_layout.visibility = View.VISIBLE
            deleteComment(context, comment)
        }
        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
        }
        //performing negative action
        builder.setNegativeButton("No") { _, _ ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun deleteComment(context:Context, comment: Comment) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val commentRepository = CommentRepository()
                val response = commentRepository.deleteComment(comment._id)
                if (response.success == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "Comment Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }
}

class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cil_Profile: ImageView = view.findViewById(R.id.cil_Profile)
    val cil_fullname: TextView = view.findViewById(R.id.cil_fullname)
    val cil_comment: TextView = view.findViewById(R.id.cil_comment)
    val cil_edit_comment: TextView = view.findViewById(R.id.cil_edit_comment)
    val cil_input_edit_comment: EditText = view.findViewById(R.id.cil_input_edit_comment)
    val cil_btn_update_comment: Button = view.findViewById(R.id.cil_btn_update_comment)
    val cil_layout: LinearLayout = view.findViewById(R.id.cil_layout)
    val cil_delete_comment: TextView = view.findViewById(R.id.cil_delete_comment)
}