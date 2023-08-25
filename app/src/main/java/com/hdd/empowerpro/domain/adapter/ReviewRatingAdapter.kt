package com.hdd.empowerpro.domain.adapter

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.ReviewRating
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.ReviewRatingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewRatingAdapter(private val jobId: String="",private val reviewRatingList: MutableList<ReviewRating>):RecyclerView.Adapter<ReviewRatingViewHolder>() {
    private var previousStarRating: Int=0
    private var selectedRating: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewRatingViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.review_item_layout,parent,false)
        return ReviewRatingViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ReviewRatingViewHolder, position: Int) {
        val review = reviewRatingList[position]
        Glide.with(holder.itemView.context).load(review.user!!.profile).circleCrop().into(holder.ril_Profile)
        holder.ril_fullname.text = review.user.fullname
        holder.ril_review.text = review.review

        previousStarRating=review.rating-1
        setRating(previousStarRating,holder.reviewRatingContainer,holder.itemView)

        holder.itemView.setOnLongClickListener {
            if(ServiceBuilder.user!!._id == review.user._id) {
                val reviewDialog = Dialog(holder.itemView.context)
                reviewDialog.setContentView(R.layout.edit_review_layout)
                reviewDialog.window?.setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val writeReview: EditText = reviewDialog.findViewById(R.id.erl_writeReview)
                val yourRatingStar: LinearLayout = reviewDialog.findViewById(R.id.erl_ratingStarContainer)
                val update: Button = reviewDialog.findViewById(R.id.erl_update)
                val delete: Button = reviewDialog.findViewById(R.id.erl_delete)

                writeReview.setText(holder.ril_review.text)
                if (selectedRating==0){
                    setRating(review.rating-1,yourRatingStar,holder.itemView)
                } else{
                    setRating(selectedRating-1,yourRatingStar,holder.itemView)
                }

                for (starValue in 0 until yourRatingStar.childCount) {
                    val starPosition: Int = starValue
                    yourRatingStar.getChildAt(starValue).setOnClickListener {
                        for (starValue in 0 until yourRatingStar.childCount) {
                            val starOnClick: ImageView = yourRatingStar.getChildAt(starValue) as ImageView
                            starOnClick.imageTintList =
                                ColorStateList.valueOf(holder.itemView.resources.getColor(R.color.starNotSelectedColor))
                            if (starValue <= starPosition) {
                                starOnClick.imageTintList =
                                    ColorStateList.valueOf(holder.itemView.resources.getColor(R.color.starSelectedColor))
                            }
                        }
                        selectedRating = starPosition +1
                    }
                }

                // update review and rating
                update.setOnClickListener {
                    if (writeReview.text.isNotEmpty()) {
                        if (selectedRating==0){
                            selectedRating=review.rating
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val reviewRatingRepository= ReviewRatingRepository()
                                reviewRatingRepository.updateReviewRating(jobId,
                                    ReviewRating(_id=review._id, review = writeReview.text.toString(), rating = selectedRating )
                                )
                            } catch (ex: Exception){
                                print(ex)
                            }
                        }
                        Toast.makeText(holder.itemView.context, "Review Updated", Toast.LENGTH_SHORT).show()
                        holder.ril_review.text =writeReview.text.toString()
                        setRating(selectedRating-1,holder.reviewRatingContainer,holder.itemView)
                        reviewDialog.dismiss()
                    } else {
                        writeReview.error = "This field is required"
                        writeReview.requestFocus()
                    }
                    reviewDialog.dismiss()
                }

                // delete review and rating
                delete.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val reviewRatingRepository= ReviewRatingRepository()
                            reviewRatingRepository.deleteReviewRating(jobId,review._id)
                        } catch (ex: Exception){
                            print(ex)
                        }
                    }
                    reviewRatingList.remove(review)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Review Deleted", Toast.LENGTH_SHORT).show()
                    reviewDialog.dismiss()
                }

                reviewDialog.show()
            } else{
                Toast.makeText(it.context, "This is not your review", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setRating(starPosition: Int, yourRatingStar:LinearLayout, itemView: View):Int {
        for (starValue in 0 until yourRatingStar.childCount) {
            val starOnClick:ImageView=yourRatingStar.getChildAt(starValue) as ImageView
            starOnClick.imageTintList=ColorStateList.valueOf(itemView.resources.getColor(R.color.starNotSelectedColor))
            if (starValue<=starPosition){
                starOnClick.imageTintList=ColorStateList.valueOf(itemView.resources.getColor(R.color.starSelectedColor))
            }
        }
        return starPosition+1
    }

    override fun getItemCount(): Int {
        return reviewRatingList.size
    }
}

class ReviewRatingViewHolder(view: View) :RecyclerView.ViewHolder(view){
    val ril_Profile:ImageView=view.findViewById(R.id.ril_Profile)
    val ril_fullname:TextView=view.findViewById(R.id.ril_fullname)
    val ril_review:TextView=view.findViewById(R.id.ril_review)
    val reviewRatingContainer: LinearLayout = view.findViewById(R.id.reviewRatingContainer)
}