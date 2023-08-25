package com.hdd.empowerpro.presentation.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Post
import com.hdd.empowerpro.repository.PostRepository
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddPostActivity : AppCompatActivity() {
    private lateinit var editRequiredPostId: String
    private lateinit var post: Post
    private var isEditMode: Boolean = false

    //Initialize
    private lateinit var post_profile_image: ImageView
    private lateinit var post_upload_image: ImageView
    private lateinit var post_status: EditText
    private lateinit var post_profile_name: TextView
    private lateinit var post_upload_switch: Switch
    private lateinit var add_post_button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        //Extract the data…
        val bundle = intent.extras
        val postId = bundle?.getString("editPostId")
        val hasPostImage = bundle?.getBoolean("hasImage")
        if (postId != null) {
            editRequiredPostId = postId
            isEditMode = true
            getPostById(editRequiredPostId)
        } else {
            editRequiredPostId = ""
            isEditMode = false
        }
        //Binding
        post_profile_image = findViewById(R.id.post_profile_image)
        post_profile_name = findViewById(R.id.post_profile_name)
        post_upload_switch = findViewById(R.id.post_upload_switch)
        post_status = findViewById(R.id.post_status)
        post_upload_image = findViewById(R.id.post_upload_image)
        add_post_button = findViewById(R.id.add_post_button)
        fetchUserData()
        post_upload_image.visibility = View.GONE

        post_upload_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                post_upload_image.visibility = View.VISIBLE
                post_upload_image.setOnClickListener {
                    loadPopUpMenu()
                }
            } else {
                post_upload_image.visibility = View.GONE
                imageUrl = null
            }
        }
        add_post_button.setOnClickListener {
            if (editRequiredPostId.isEmpty()) {
                addPost()
            }
        }
        if (editRequiredPostId.isNotEmpty()) {
            add_post_button.text = "Update Post"
            post_upload_image.setOnClickListener {
                loadPopUpMenu()
            }
            add_post_button.setOnClickListener {
                if (hasPostImage == false) {
                    updatePostWithoutImage(editRequiredPostId)
                } else {
                    if (imageUrl != null) {
                        updatePostWithImage(editRequiredPostId)
                    } else {
                        updatePostWithoutImage(editRequiredPostId)
                    }
                }
            }
        }
    }

    private fun getPostById(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = PostRepository()
                val response = userRepository.getPostById(postId)
                if (response.success == true) {
                    post = response.data!!
                    withContext(Dispatchers.Main) {
                        post_upload_switch.visibility = View.INVISIBLE
                        if (post.image != "") {
                            post_upload_image.visibility = View.VISIBLE
                            post_status.setText(post.status!!)
                            Glide.with(this@AddPostActivity).load(post.image!!)
                                .into(post_upload_image)
                        } else {
                            post_upload_image.visibility = View.GONE
                            post_status.setText(post.status!!)
                        }
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun fetchUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserProfile()
                if (response.success == true) {
                    val user = response.data!!
                    withContext(Dispatchers.Main) {
                        Glide.with(this@AddPostActivity).load(user.profile).circleCrop()
                            .into(post_profile_image)
                        post_profile_name.text = user.fullname
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    // Load pop up menu
    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this, post_upload_image)
        popupMenu.menuInflater.inflate(R.menu.gallery_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCamera -> openCamera()
                R.id.menuGallery -> openGallery()
            }
            true
        }
        popupMenu.show()
    }

    private var REQUEST_GALLERY_CODE = 0
    private var REQUEST_CAMERA_CODE = 1
    private var imageUrl: String? = null
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_CODE && data != null) {
                val selectedImage: Uri? = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = contentResolver
                val cursor =
                    contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imageUrl = cursor.getString(columnIndex)
                post_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                post_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
            }
        }
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun addPost() {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val mimeType = getMimeType(file.path);
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)   // fragment
            val reqStatus = RequestBody.create(
                "multipart/fetch-data".toMediaTypeOrNull(),
                post_status.text.toString()
            )
            val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val postRepository = PostRepository()
                    val uploadedResponse = postRepository.addPostWithImage(reqStatus, body)
                    if (uploadedResponse.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddPostActivity,"Post Added Successfully",Toast.LENGTH_SHORT).show()
                            super.onBackPressed()
                        }
                    }
                } catch (ex: Exception) {
                    print(ex)
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val postRepository = PostRepository()
                    val response = postRepository.addPostWithoutImage(Post(status = post_status.text.toString()))
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddPostActivity,"Post Added Successfully",Toast.LENGTH_SHORT).show()
                            super.onBackPressed()
                        }
                    }
                } catch (ex: Exception) {
                    print(ex)
                }
            }
        }
        super.onBackPressed()
    }

    private fun updatePostWithImage(postId: String) {
        val file = File(imageUrl!!)
        val mimeType = getMimeType(file.path);
        val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)   // fragment
        val reqStatus = RequestBody.create(
            "multipart/fetch-data".toMediaTypeOrNull(),
            post_status.text.toString()
        )
        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postRepository = PostRepository()
                val uploadedResponse = postRepository.updatePostWithImage(postId, reqStatus, body)
                if (uploadedResponse.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddPostActivity,"Post Updated Successfully",Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun updatePostWithoutImage(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postRepository = PostRepository()
                val response = postRepository.updatePostWithoutImage(postId,Post(status = post_status.text.toString())                    )
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddPostActivity,"Post Updated Successfully",Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos) // YOU can also save it in JPEG
            val bitMapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitMapData)
            fos.flush()
            fos.close()
            file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
}