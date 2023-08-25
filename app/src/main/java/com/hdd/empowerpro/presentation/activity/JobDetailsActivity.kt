package com.hdd.empowerpro.presentation.activity

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Job
import com.hdd.empowerpro.data.models.ReviewRating
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.ReviewRatingAdapter
import com.hdd.empowerpro.domain.adapter.SetFragmentAdapter
import com.hdd.empowerpro.presentation.fragments.ViewJobDetailFragment
import com.hdd.empowerpro.presentation.fragments.ViewJobDirectionsFragment
import com.hdd.empowerpro.repository.JobRepository
import com.hdd.empowerpro.repository.ReviewRatingRepository
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobDetailsActivity : AppCompatActivity() {
    private lateinit var job: Job

    // for job image layout
    private lateinit var ril_job_name:TextView
    private lateinit var ril_job_image:ImageView
    private lateinit var ril_job_share:ImageView
    private lateinit var ril_job_description:TextView
    private lateinit var pil_saved: ImageView

    // for job description layout
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    // for review-rating layout
    private lateinit var ard_add_review: Button
    private lateinit var reviewRecyclerView: RecyclerView
    private lateinit var ril_hashtag: TextView
    private lateinit var reviewList: MutableList<ReviewRating>
    private lateinit var reviewAdapter: ReviewRatingAdapter
    private lateinit var yourRatingStar: LinearLayout

    private var isJobSaved: Boolean = false
    private var selectedRating: Int = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)
        //Extract the data…
        val bundle = intent.extras
        val requiredJobId = bundle!!.getString("jobId")!!
        getJobById(requiredJobId)

        // for job image layouts
        ril_job_name=findViewById(R.id.ril_job_name)
        ril_job_image=findViewById(R.id.ril_job_image)
        ril_job_share=findViewById(R.id.ril_job_share)
        ril_job_description=findViewById(R.id.ril_job_description)
        pil_saved = findViewById(R.id.ril_job_saved)
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        yourRatingStar = findViewById(R.id.yourRatingStar)
        ril_hashtag = findViewById(R.id.ril_hashtag)

        // for Saved job action button
        pil_saved.setOnClickListener {
            if (isJobSaved) {
                setColor(R.color.unlikeColor)
                savedJob(requiredJobId, false, R.color.unlikeColor)
            } else {
                setColor(R.color.likeColor)
                savedJob(requiredJobId, true, R.color.likeColor)
            }
        }

        // for share job
        ril_job_share.setOnClickListener {
            showAlertDialog(requiredJobId)
        }

        // for job description layout
        viewPager = findViewById(R.id.rdl_viewPager)
        tabLayout = findViewById(R.id.rdl_tabLayout)

        // for job review-rating layout
        ard_add_review = findViewById(R.id.ard_add_review)

        // add review button
        ard_add_review.setOnClickListener {
            val reviewDialog = Dialog(this)
            reviewDialog.setContentView(R.layout.add_review_layout)
            reviewDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val writeReview: EditText = reviewDialog.findViewById(R.id.arl_writeReview)
            val cancel: Button = reviewDialog.findViewById(R.id.arl_cancel)
            val ok: Button = reviewDialog.findViewById(R.id.arl_ok)
            val yourRatingStar: LinearLayout = reviewDialog.findViewById(R.id.ratingStarContainer)
            cancel.setOnClickListener {
                reviewDialog.dismiss()
            }
//            setRating(starRating)
            for (starValue in 0 until yourRatingStar.childCount) {
                val starPosition: Int = starValue
                yourRatingStar.getChildAt(starValue).setOnClickListener {
                    for (starValue in 0 until yourRatingStar.childCount) {
                        val starOnClick: ImageView =
                            yourRatingStar.getChildAt(starValue) as ImageView
                        starOnClick.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.starNotSelectedColor))
                        if (starValue <= starPosition) {
                            starOnClick.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.starSelectedColor))
                        }
                    }
                    selectedRating = starPosition + 1
                }
            }
            ok.setOnClickListener {
                if (writeReview.text.isNotEmpty()) {
                    addReview(requiredJobId, writeReview.text.toString(), selectedRating)
                    reviewDialog.dismiss()
                } else {
                    writeReview.error = "This field is required"
                }
            }
            reviewDialog.show()
        }
    }

    private fun shareJob(jobId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobRepository= JobRepository()
                val response = jobRepository.shareJob(jobId!!)
                if(response.success == true){
                    Toast.makeText(this@JobDetailsActivity, "Job Shared", Toast.LENGTH_SHORT).show()
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    private fun savedJob(jobId: String, updateValue: Boolean,updateColor: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository= UserRepository()
                val response=userRepository.savedJob(jobId)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        isJobSaved=updateValue
                        setColor(updateColor)
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    private fun showAlertDialog(jobId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Share Job")
        builder.setMessage("Do you want to share this job?")
        builder.setIcon(R.drawable.share_icon)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            shareJob(jobId)
            finish()
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

    private fun setColor(givenColor: Int) {
        pil_saved.setColorFilter(resources.getColor(givenColor))
    }

    private fun getJobById(jobId: String) {
        reviewList= mutableListOf<ReviewRating>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobRepository= JobRepository()
                val response=jobRepository.getJobById(jobId!!)
                if(response.success==true){
                    job = response.data!!
                    withContext(Dispatchers.Main){
                        setAvgRating(job.avgRating!! - 1)
                        ril_job_name.text = job.title
                        ril_job_description.text= job.description
                        if(job.hashtag?.size != 0){
                            ril_hashtag.visibility = View.VISIBLE
                            var hashtagText = ""
                            for(hashtag in job.hashtag!!){
                                hashtagText = "$hashtagText #$hashtag"
                            }
                            ril_hashtag.text = hashtagText
                        }
                        Glide.with(this@JobDetailsActivity).load(job.image).into(ril_job_image)

                        tabTitleList = arrayListOf<String>("Job Detail", "Job Directions")
                        fragmentList = arrayListOf<Fragment>(
                            ViewJobDetailFragment(job.jobDetailSchema!!),
                            ViewJobDirectionsFragment(job.direction!!)
                        )

                        // setting up adapter class for view pager2 in PDL
                        val adapter = SetFragmentAdapter(fragmentList, supportFragmentManager, lifecycle)
                        viewPager.adapter = adapter
                        TabLayoutMediator(tabLayout, viewPager) {
                                tab, position -> tab.text = tabTitleList[position]
                        }.attach()
                        reviewList = job.review!!
                        reviewAdapter = ReviewRatingAdapter(job._id,reviewList.asReversed())

                        val linearLayoutManager= object : LinearLayoutManager(this@JobDetailsActivity) {
                            override fun canScrollVertically(): Boolean {
                                return true
                            }
                        }
                        reviewRecyclerView.layoutManager= linearLayoutManager
                        reviewRecyclerView.adapter=reviewAdapter
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAvgRating(starPosition: Int):Int {
        for (starValue in 0 until yourRatingStar.childCount) {
            val starOnClick:ImageView=yourRatingStar.getChildAt(starValue) as ImageView
            starOnClick.imageTintList=ColorStateList.valueOf(resources.getColor(R.color.starNotSelectedColor))
            if (starValue<=starPosition){
                starOnClick.imageTintList=ColorStateList.valueOf(resources.getColor(R.color.starSelectedColor))
            }
        }
        return starPosition+1
    }

    private fun addReview(jobId:String,reviewMessage:String,ratingNumber:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reviewRepository = ReviewRatingRepository()
                val response = reviewRepository.addReviewRating(jobId, ReviewRating(review = reviewMessage, rating = ratingNumber))
                if (response.success==true){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@JobDetailsActivity,"ReviewRating Added Successfully",Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex:Exception){
                print(ex)
            }
        }
        reviewList.add(ReviewRating(review = reviewMessage,user = ServiceBuilder.user, rating = selectedRating))
        reviewAdapter.notifyDataSetChanged()
    }
}