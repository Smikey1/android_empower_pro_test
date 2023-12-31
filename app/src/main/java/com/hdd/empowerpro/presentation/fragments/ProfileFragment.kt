package com.hdd.empowerpro.presentation.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.ViewPager2Adapter
import com.hdd.empowerpro.presentation.activity.*
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment() : Fragment() {

    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var profileSetting: ImageButton

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var postCounter: TextView
    private lateinit var appliedCounter: TextView
    private lateinit var savedCounter: TextView
    private lateinit var profileDescription: TextView
    private lateinit var profileWebsite: TextView
    private lateinit var tv_followers: TextView
    private lateinit var tv_following: TextView

    // for company layout
    private lateinit var companyImage: ImageView
    private lateinit var companyName: TextView
    private lateinit var companyLayout: ConstraintLayout

    private var userId: String? = null

    override fun onResume() {
        fetchData()
        super.onResume()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // for profile layout
        viewPager = view.findViewById(R.id.profile_viewPager)
        tabLayout = view.findViewById(R.id.profile_tabLayout)
        profileSetting = view.findViewById(R.id.profileSetting)
        profileImage = view.findViewById(R.id.profile_image)
        profileName = view.findViewById(R.id.profile_name)
        postCounter = view.findViewById(R.id.postCounter)
        appliedCounter = view.findViewById(R.id.appliedCounter)
        savedCounter = view.findViewById(R.id.savedCounter)
        profileDescription = view.findViewById(R.id.profile_description)
        profileWebsite = view.findViewById(R.id.profile_website)
        tv_followers = view.findViewById(R.id.tv_followers)
        tv_following = view.findViewById(R.id.tv_following)

        // for company layout
        companyImage = view.findViewById(R.id.companyImage)
        companyName = view.findViewById(R.id.companyName)
        companyLayout = view.findViewById(R.id.companyLayout)


        fetchData()
        tabTitleList = arrayListOf<String>("Created", "Applied", "Saved")
        fragmentList = arrayListOf<Fragment>(
            CreatedJobFragment(),
            AppliedJobFragment(),
            SavedJobFragment()
        )

        // setting up adapter class for view pager2
        val adapter = ViewPager2Adapter(fragmentList, parentFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) {
                tab, position -> tab.text = tabTitleList[position]
        }.attach()

        profileSetting.setOnClickListener {
            loadPopUpSetting()
        }
        tv_following.setOnClickListener {
            navigateToFollowerActivity()
        }

        tv_followers.setOnClickListener {
            navigateToFollowerActivity()
        }
        
        companyLayout.setOnClickListener {
            val intent=Intent(requireContext(), ViewCompanyActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun navigateToFollowerActivity(){
        val intent = Intent(requireContext(), FollowingActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserProfile()
                if (response.success == true) {
                    val user = response.data!!
                    userId = user._id
                    ServiceBuilder.user=user
                    withContext(Dispatchers.Main) {
                        Glide.with(requireContext()).load(user.profile).circleCrop().into(profileImage)
                        profileName.text = user.fullname
                        if(user.bio == ""){
                            profileDescription.visibility = View.GONE
                        }
                        if(user.website == ""){
                            profileWebsite.visibility = View.GONE
                        }

                        if(user.company==null){
                            companyLayout.visibility = View.GONE
                        } else{
                            companyLayout.visibility = View.VISIBLE
                            Glide.with(requireContext()).load(user.company!!.image).circleCrop().into(companyImage)
                            companyName.text= user.company!!.name
                        }

                        profileDescription.text = user.bio
                        // Set the text to be underlined and blue in color

                        val text = user.website.toString()
                        val spannableString = SpannableString(text)

                        // Apply underline style to the text
                        val underlineSpan = UnderlineSpan()
                        spannableString.setSpan(underlineSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        // Apply blue color to the text
                        val blueColor = Color.BLUE
                        val foregroundColorSpan = ForegroundColorSpan(blueColor)
                        spannableString.setSpan(foregroundColorSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        profileWebsite.text = spannableString
                        postCounter.text=user.post!!.size.toString()
                        savedCounter.text=user.savedJob!!.size.toString()
                        appliedCounter.text=user.appliedJob!!.size.toString()
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }
    private fun loadPopUpSetting() {
        val popMenu = PopupMenu(requireContext(), profileSetting)
        popMenu.menuInflater.inflate(R.menu.profile_menu, popMenu.menu)
        popMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuEditProfile -> startActivity(Intent(requireContext(), EditAccountActivity::class.java))
                R.id.menuRecentlyViewed -> startActivity(Intent(requireContext(), RecentlyViewedActivity::class.java))
//                R.id.menuViewArchive -> startActivity(Intent(requireContext(), ViewArchivedActivity::class.java))
                R.id.menuLogOut ->  showAlertDialog()
            }
            true
        }
        popMenu.show()
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logging out?")
        builder.setMessage("Are you sure want to Logout?")
        builder.setIcon(R.drawable.sign_out)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            val sharedPreferences = context?.getSharedPreferences("userAuth", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()!!
            editor.putString("token", "")
            editor.putString("email", "")
            editor.putString("password", "")
            editor.apply()
            requireActivity().finish()
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
}