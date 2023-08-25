package com.hdd.empowerpro.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.SetFragmentAdapter
import com.hdd.empowerpro.domain.adapter.ViewPager2Adapter
import com.hdd.empowerpro.presentation.fragments.AddJobDetailFragment
import com.hdd.empowerpro.presentation.fragments.AddJobDirectionsFragment
import com.hdd.empowerpro.presentation.fragments.AddJobHashtagsFragment
import com.hdd.empowerpro.repository.JobRepository
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

class AddJobActivity : AppCompatActivity() {
    private lateinit var editRequiredJobId: String
    private lateinit var previewJobData: Job
    private lateinit var getJob: Job

    private var isEditMode: Boolean=false
    private var isImageUpload: Boolean=false
    //Initialize
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var job_profile_image: ImageView
    private lateinit var job_upload_image: ImageView
    private lateinit var job_profile_name: TextView

    // for job add and discard
    private lateinit var job_post: ImageView
    private lateinit var job_discard: ImageView

    // for upload job
    private lateinit var job_title: EditText
    private lateinit var job_description: EditText
    private lateinit var next_add_job_button: Button
    private lateinit var jobUploadLinearLayout: LinearLayout
    //for preview job
    private lateinit var preview_job_title: TextView
    private lateinit var preview_job_description: TextView
    private lateinit var preview_job_upload_image: ImageView
    private lateinit var preview_add_job_button: Button
    private lateinit var jobPreviewLinearLayout: LinearLayout
    //add more job details layout
    private lateinit var segment2LinearLayout: LinearLayout
    private lateinit var aar_ll_post_job: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_job)
        job_profile_image = findViewById(R.id.job_profile_image)
        job_profile_name = findViewById(R.id.job_profile_name)
        // for upload job

        //Extract the data…
        val bundle = intent.extras
        val jobId = bundle?.getString("editJobId")
        if (jobId != null) {
            editRequiredJobId=jobId
            isEditMode=true
            getJobById(editRequiredJobId)
        } else{
            editRequiredJobId= ""
            isEditMode=false
        }

        // for job add and discard
        job_discard = findViewById(R.id.job_discard)
        job_post = findViewById(R.id.job_post)

        job_title = findViewById(R.id.job_title)
        job_description = findViewById(R.id.job_description)
        job_upload_image = findViewById(R.id.job_upload_image)
        next_add_job_button = findViewById(R.id.next_add_job_button)
        jobUploadLinearLayout = findViewById(R.id.jobUploadLinearLayout)
        //for preview job
        preview_job_title = findViewById(R.id.preview_job_title)
        preview_job_description = findViewById(R.id.preview_job_description)
        preview_job_upload_image = findViewById(R.id.preview_job_upload_image)
        preview_add_job_button = findViewById(R.id.preview_add_job_button)
        jobPreviewLinearLayout = findViewById(R.id.jobPreviewLinearLayout)
        // add more job details layout
        segment2LinearLayout = findViewById(R.id.segment2LinearLayout)
        aar_ll_post_job = findViewById(R.id.aar_ll_post_job)
        // for profile layout
        Glide.with(this@AddJobActivity).load(ServiceBuilder.user!!.profile).circleCrop()
            .into(job_profile_image)
        job_profile_name.text = ServiceBuilder.user!!.fullname

        job_discard.setOnClickListener {
            showAlertDialog()
        }

        job_post.setOnClickListener {
            if (editRequiredJobId.isEmpty()){
                postJob()

//                super.onBackPressed()
            }
        }

        viewPager = findViewById(R.id.rdl_viewPager)
        tabLayout = findViewById(R.id.rdl_tabLayout)
        tabTitleList = arrayListOf<String>("Hashtag", "Details", "Direction")
        fragmentList = arrayListOf<Fragment>(
            AddJobHashtagsFragment(),
            AddJobDetailFragment(),
            AddJobDirectionsFragment(),
        )
        // setting up adapter class for view pager2
        val adapter = ViewPager2Adapter(fragmentList, supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()
        job_upload_image.setOnClickListener {
            loadPopUpMenu()
        }
        next_add_job_button.setOnClickListener {
            when {
                job_title.text.isEmpty() -> {job_title.error="Job Name is required"
                    job_title.requestFocus()}
                job_description.text.isEmpty() -> { job_description.error="Job Description is required"
                    job_description.requestFocus()}
                isImageUpload == false -> {if (editRequiredJobId.isEmpty()){
                    Toast.makeText(this,"Image is required",Toast.LENGTH_SHORT).show()
                }}
                else -> {
                    addJob(ServiceBuilder.jobId!!,job_title.text.toString(), job_description.text.toString())
                    jobUploadLinearLayout.visibility=View.GONE
                    jobPreviewLinearLayout.visibility=View.VISIBLE
                    segment2LinearLayout.visibility=View.VISIBLE
                    aar_ll_post_job.visibility=View.VISIBLE
                    next_add_job_button.visibility=View.GONE
                    preview_add_job_button.visibility=View.VISIBLE
                    hideKeyboard(next_add_job_button)
                    showPreviewData(job_title.text.toString(), job_description.text.toString())
                }
            }
        }
        preview_add_job_button.setOnClickListener {
            jobUploadLinearLayout.visibility=View.VISIBLE
            jobPreviewLinearLayout.visibility=View.GONE
            segment2LinearLayout.visibility=View.GONE
            preview_add_job_button.visibility=View.GONE
            next_add_job_button.visibility=View.VISIBLE
        }
    }

    override fun onBackPressed() {
        showAlertDialog()
    }

    private fun discardJob() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobRepository= JobRepository()
                val response=jobRepository.discardJob(ServiceBuilder.jobId!!)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        ServiceBuilder.jobId=""
                        super.onBackPressed()
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }
    private fun postJob() {
        if (editRequiredJobId.isEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val jobRepository= JobRepository()
                    val response=jobRepository.postJob(ServiceBuilder.jobId!!)
                    if(response.success==true){
                        withContext(Dispatchers.Main){
                            ServiceBuilder.jobId=""
                            super.onBackPressed()
                        }
                    }
                }catch (ex:Exception){
                    print(ex)
                }
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discard Job")
        builder.setIcon(R.drawable.cross)
        builder.setMessage("Are you sure to discard this job?")

        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            if (editRequiredJobId.isEmpty()){
                discardJob()
                super.onBackPressed()
            } else {
                Toast.makeText(this, "Changes are saved", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
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

    // Load pop up menu
    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this, job_upload_image)
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
                job_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                preview_job_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                isImageUpload=true
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                job_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                preview_job_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                isImageUpload=true
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

    private fun addJob(prevJobId:String,jobName:String,jobDescription: String) {
        if (isImageUpload) {
            if (imageUrl!=null){
                val file = File(imageUrl!!)
                val mimeType = getMimeType(file.path);
                val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
//              val reqFiles = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)   // fragment
                val reqJobTitle = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), jobName)
                val reqJobDescription = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(),jobDescription)
                val reqPrevJobId = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), prevJobId)
                val body = MultipartBody.Part.createFormData("image",file.name,reqFile)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val jobRepository = JobRepository()
                        val response = jobRepository.addJob(
                            body,
                            reqJobTitle,
                            reqJobDescription,
                            reqPrevJobId
                        )
                        if (response.success == true) {
                            withContext(Dispatchers.Main) {
                                previewJobData = response.data!!
                                ServiceBuilder.jobId = previewJobData._id
                            }
                        }
                    } catch (ex: Exception) {
                        withContext(Dispatchers.Main) {
                            print(ex)
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val jobRepository = JobRepository()
                        val response = jobRepository.updateJobWithoutImage(prevJobId,Job(title =jobName, description =  jobDescription))
                        if (response.success == true) {
                            withContext(Dispatchers.Main) {
                                previewJobData = response.data!!
                                ServiceBuilder.jobId = previewJobData._id
                            }
                        }
                    } catch (ex: Exception) {
                        withContext(Dispatchers.Main) {
                            print(ex)
                        }
                    }
                }
            }
        }
    }

    private fun showPreviewData(inputTitle: String, inputDescription: String) {
        preview_job_title.text = inputTitle
        preview_job_description.text = inputDescription
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getJobById(jobId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobRepository = JobRepository()
                val response = jobRepository.getJobById(jobId!!)
                if (response.success == true) {
                    getJob = response.data!!
                    withContext(Dispatchers.Main) {
                        ServiceBuilder.jobId=getJob._id
                        job_title.setText(getJob.title)
                        job_description.setText(getJob.description)
                        isImageUpload=true
                        Glide.with(this@AddJobActivity).load(getJob.image).into(job_upload_image)
                        Glide.with(this@AddJobActivity).load(getJob.image).into(preview_job_upload_image)

                        tabTitleList = arrayListOf<String>("Detail", "Directions")
                        fragmentList = arrayListOf<Fragment>(
                            AddJobDetailFragment(isEditMode,getJob.jobDetailSchema!!),
                            AddJobDirectionsFragment(isEditMode, getJob.direction!!)
                        )

                        // setting up adapter class for view pager2 in PDL
                        val adapter =
                            SetFragmentAdapter(fragmentList, supportFragmentManager, lifecycle)
                        viewPager.adapter = adapter
                        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                            tab.text = tabTitleList[position]
                        }.attach()
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