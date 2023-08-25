package com.hdd.empowerpro.presentation.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Comment
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.CommentAdapter
import com.hdd.empowerpro.repository.CommentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCommentActivity : AppCompatActivity() {
    private lateinit var commentList: MutableList<Comment>
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var fac_Profile: ImageView
    private lateinit var fac_etAddComment: EditText
    private lateinit var fac_btnAddComment: Button
    private lateinit var fac_CommentRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_comment)
        fac_Profile = findViewById(R.id.fac_Profile)
        fac_etAddComment = findViewById(R.id.fac_etAddComment)
        fac_btnAddComment = findViewById(R.id.fac_btnAddComment)
        fac_CommentRV = findViewById(R.id.fac_CommentRV)
        val bundle = intent.extras
        val requiredRecipeId = bundle!!.getString("myId")!!
        getAllCommentByPostId(requiredRecipeId)
        fac_btnAddComment.setOnClickListener {
            if (fac_etAddComment.text.isNotEmpty()){
                addComment(requiredRecipeId,fac_etAddComment.text.toString())
                fac_etAddComment.setText("")
                getAllCommentByPostId(requiredRecipeId)
            } else{
                fac_etAddComment.error="This is required"
            }
        }
    }
    private fun getAllCommentByPostId(postId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val commentRepository= CommentRepository()
                val response=commentRepository.getAllComment(postId!!)
                if(response.success==true){
                    commentList = response.data!!
                    withContext(Dispatchers.Main){
                        Glide.with(this@AddCommentActivity).load(ServiceBuilder.user?.profile).circleCrop().into(fac_Profile)
                        commentAdapter = CommentAdapter(commentList.asReversed())
                        val linearLayoutManager= object : LinearLayoutManager(this@AddCommentActivity) {
                            override fun canScrollVertically(): Boolean {
                                return true
                            }
                        }
                        fac_CommentRV.layoutManager= linearLayoutManager
                        fac_CommentRV.adapter=commentAdapter
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }
    private fun addComment(postId:String,comment:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val commentRepository = CommentRepository()
                val response = commentRepository.addComment(postId, Comment(comment = comment))
                if (response.success==true){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@AddCommentActivity,"Comment Added Successfully",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex:Exception){
                print(ex)
            }
        }
        commentList.add(Comment(comment = comment,user = ServiceBuilder.user))
        commentAdapter.notifyDataSetChanged()
    }
}