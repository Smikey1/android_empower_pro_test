package com.hdd.empowerpro.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.domain.adapter.CustomSpinnerAdapter
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
import kotlin.collections.HashMap

class EditAccountActivity : AppCompatActivity() {

    private lateinit var fullname: TextView
    private lateinit var etFullname: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var spinnerDropdownPhone: Spinner

    private lateinit var bio: TextView
    private lateinit var userBio: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var address: EditText
    private lateinit var website: EditText
    private lateinit var newPassword: EditText
    private lateinit var oldPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button

    private lateinit var profileImage: ImageView
    private lateinit var iv_add_img: ImageView

    private var userId: String? = null
    private var requiredPassword: String? = null

    private var savedPassword: String? = ""

    private var REQUEST_GALLERY_CODE = 0
    private var REQUEST_CAMERA_CODE = 1
    private var imageUrl: String? = null

    private var isPasswordChanged: Boolean = false

    val fullPhoneNumber = hashMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)


        fullname = findViewById(R.id.aea_fullname)
        progressBar = findViewById(R.id.aea_pb)
        spinnerDropdownPhone = findViewById(R.id.spinnerCountryCodes)

        etFullname = findViewById(R.id.aea_user_full_name)
        bio = findViewById(R.id.aea_bio)
        userBio = findViewById(R.id.aea_user_bio)
        phone = findViewById(R.id.aea_user_phone_number)
        email = findViewById(R.id.aea_user_email)
        address = findViewById(R.id.aea_user_address)
        website = findViewById(R.id.aea_user_website)
        newPassword = findViewById(R.id.aea_new_password)
        oldPassword = findViewById(R.id.aea_old_password)
        confirmPassword = findViewById(R.id.aea_confirm_password)
        btnUpdate = findViewById(R.id.btnUpdateProfile)
        btnCancel = findViewById(R.id.aea_btnCancel)

        profileImage = findViewById(R.id.aea_profile_image)
        iv_add_img = findViewById(R.id.aea_add_profile)

        iv_add_img.setOnClickListener {
            loadPopUpMenu()
        }

        btnCancel.setOnClickListener {
           super.onBackPressed()
        }

        val countryCodesMap = getHashMapOfCountryCode()
        // Create custom ArrayAdapter with the countryCodesMap keys (country codes)
        val spinnerAdapter = CustomSpinnerAdapter(this@EditAccountActivity, R.layout.spinner_dropdown_item, countryCodesMap)
        // Apply the adapter to the spinner
        spinnerDropdownPhone.adapter = spinnerAdapter

        // Assuming you have fetched the value to be set as the initial value
        val fetchedValue = ServiceBuilder.user!!.countryCode

        // Find the position of the fetched value in the list
        val position = countryCodesMap.keys.toList().indexOf(fetchedValue)

        // Set the selected item in the Spinner programmatically
        if (position != -1) {
            spinnerDropdownPhone.setSelection(position)
        }

        // Set an item selected listener to display only the country code after selection
        spinnerDropdownPhone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val countryCode = spinnerDropdownPhone.selectedItem.toString()
                val tvCountryCodeDropdown: TextView = view?.findViewById(R.id.tvCountryCodeDropdown) ?: return
                tvCountryCodeDropdown.text = countryCode
                val phoneNumber = phone.text.toString()
                fullPhoneNumber["countryCode"]=countryCode
                fullPhoneNumber["phone"]=phoneNumber
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected
            }
        }

        btnUpdate.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            retrieveUserDetails()
            checkForPasswordChange()
            checkForEmailAndPhoneChange()
            updateUserDetails()
        }

        fetchData()

    }
    private fun getHashMapOfCountryCode(): HashMap<String,String>{
        val countryCodesArray = resources.getStringArray(R.array.country_codes)
        val countryCodesMap = hashMapOf<String, String>()

        for (code in countryCodesArray) {
            val countryCodeStartIndex = code.indexOf("+")
            val countryCodeEndIndex = code.indexOf(" (")
            if (countryCodeStartIndex != -1 && countryCodeEndIndex != -1 && countryCodeEndIndex > countryCodeStartIndex) {
                val countryCode = code.substring(countryCodeStartIndex, countryCodeEndIndex).trim()
                val countryName = code.substring(countryCodeEndIndex + 2, code.length - 1).trim()
                countryCodesMap[countryCode] = countryName
            }
        }
        return countryCodesMap
    }

    @SuppressLint("ResourceAsColor")
    private fun checkForPasswordChange() {
        if (!TextUtils.isEmpty(oldPassword.text)&& !TextUtils.isEmpty(newPassword.text)&&!TextUtils.isEmpty(confirmPassword.text)) {
            isPasswordChanged=true
            if ( newPassword.length() >= 5 && confirmPassword.length() >= 5) {
                if (newPassword.text.toString() == confirmPassword.text.toString()) {
                    requiredPassword = newPassword.text.toString()
                } else {
                    confirmPassword.error = "Password doesn't match"
                    progressBar.visibility=View.INVISIBLE
                }
            } else {
                confirmPassword.error = "Password length should be greater than 4 characters"
                progressBar.visibility=View.INVISIBLE
            }
        } else {
            isPasswordChanged=false
        }
    }

    private fun checkForEmailAndPhoneChange() {
        if (email.text.toString()!=ServiceBuilder.user?.email || phone.text.toString()!=ServiceBuilder.user?.phone){
            resetUserCodeForUpdateEmailPhone()
        }
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserProfile()
                if (response.success == true) {
                    val user = response.data!!
                    userId = user._id
                    withContext(Dispatchers.Main) {
                        Glide.with(this@EditAccountActivity).load(user.profile).circleCrop()
                            .into(profileImage)
                        fullname.text = user.fullname
                        etFullname.setText(user.fullname)
                        bio.text = user.bio
                        userBio.setText(user.bio)
                        phone.setText(user.phone)
                        website.setText(user.website)
                        email.setText(user.email)
                        address.setText(user.address)
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun updateUserDetails(){
        val user = User(
            fullname = etFullname.text.toString(),
            address = address.text.toString(),
            website = website.text.toString(),
            password = requiredPassword,
            bio = userBio.text.toString()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.updateUserProfile(user)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        fullname.text = etFullname.text.toString()
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@EditAccountActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    // Load pop up menu
    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this, iv_add_img)
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
                profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
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

    private fun uploadImage() {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val mimeType = getMimeType(file.path);
//            val reqFile = RequestBody.create(mimeType?.let { it.toMediaTypeOrNull() }, file)
//            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)  // activity
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)   // fragment
            val body = MultipartBody.Part.createFormData("profile", file.name, reqFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRepository = UserRepository()
                    val response = userRepository.uploadImage(body)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditAccountActivity, "Uploaded", Toast.LENGTH_SHORT)
                                .show()
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

    private fun bitmapToFile( bitmap: Bitmap, fileNameToSave: String ): File? {
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

    private fun resetUserCodeForUpdateEmailPhone(){
        val user = User(email = email.text.toString(), countryCode = fullPhoneNumber["countryCode"], phone = phone.text.toString())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.resetUserCodeForUpdateEmailPhone(user)
                if (response.success == true) {
                    withContext(Dispatchers.Main){
                        showOTPVerificationAlertDialog()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAccountActivity,ex.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // space for change password
    private fun changePassword(user: User){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.changePassword(user)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditAccountActivity, "Session has expired, Login Again!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@EditAccountActivity, LoginActivity::class.java))
                        Toast.makeText(this@EditAccountActivity, "Password Changed", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE);
                        val editor = sharedPreferences.edit()
                        editor.putString("password", "")
                        editor.putString("token", "")
                        editor.clear()
                        editor.apply()
                        finish()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAccountActivity,ex.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun retrieveUserDetails() {
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        savedPassword = sharedPreferences.getString("password", "")
    }

    private fun showOTPVerificationAlertDialog() {
        val otpDialog = Dialog(this)
        otpDialog.setContentView(R.layout.otp_verification_dialog)
        otpDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val otpDigit1: EditText = otpDialog.findViewById(R.id.otpDigit1)
        val otpDigit2: EditText = otpDialog.findViewById(R.id.otpDigit2)
        val otpDigit3: EditText = otpDialog.findViewById(R.id.otpDigit3)
        val otpDigit4: EditText = otpDialog.findViewById(R.id.otpDigit4)
        val otpDigit5: EditText = otpDialog.findViewById(R.id.otpDigit5)
        val otpDigit6: EditText = otpDialog.findViewById(R.id.otpDigit6)

        val tvOTPVerificationTitle: TextView = otpDialog.findViewById(R.id.tvOTPVerificationTitle)
        tvOTPVerificationTitle.text = "OTP Verification"
        val tvOTPDescription: TextView = otpDialog.findViewById(R.id.tvOTPDescription)
        tvOTPDescription.text="It seems like you have changed your Email or Phone\nEnter the OTP sent to your email/number"
        val progressBar: ProgressBar = otpDialog.findViewById(R.id.pb_otp)
        val cancel: Button = otpDialog.findViewById(R.id.otp_cancel)
        val btnVerify: Button = otpDialog.findViewById(R.id.otp_verify)

        val resendOtp: TextView = otpDialog.findViewById(R.id.tvResendOTPText)

        val otpDigits: Array<EditText> = arrayOf(
            otpDigit1,
            otpDigit2,
            otpDigit3,
            otpDigit4,
            otpDigit5,
            otpDigit6
        )

        for (i in otpDigits.indices) {
            otpDigits[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpDigits.size - 1) {
                        otpDigits[i + 1].requestFocus()
                    }
                }
            })

            // Set up TextWatcher to handle backspace key press
            otpDigits[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.action == android.view.KeyEvent.ACTION_UP) {
                    // Move focus to the previous EditText field when backspace is pressed
                    if (i > 0) {
                        otpDigits[i - 1].requestFocus()
                    }
                }
                false
            }
        }

        cancel.setOnClickListener {
            executeOtherUpdates()
            otpDialog.dismiss();
        }

        resendOtp.setOnClickListener {
            showToast("Otp Sent")
            val threeMinutesInMillis: Long = 1 * 60 * 1000
            startCountDownTimer(threeMinutesInMillis,resendOtp)
        }

        btnVerify.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val enteredOTP = getCombinedOTP(otpDigits)
            val user = User(email = email.text.toString(), countryCode = fullPhoneNumber["countryCode"]!!, phone = phone.text.toString(), resetCode = enteredOTP)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRepository = UserRepository()
                    val response = userRepository.updateUserEmailOrPhone(user)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            showToast("OTP Verified!")
                            saveUserDetailToSharedPref(email.text.toString())
                            Toast.makeText(this@EditAccountActivity, response.message, Toast.LENGTH_SHORT).show()
                            otpDialog.dismiss()
                            executeOtherUpdates()
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        showToast("Invalid OTP! Please try again.")
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditAccountActivity,ex.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        otpDialog.setCancelable(false)

        otpDialog.show()
    }

    private fun executeOtherUpdates(){
        executeFunctionAfterDelay(500L){
            updateUserDetails()
            if (imageUrl != null) {
                uploadImage()
                super.onBackPressed()
            }

            if (isPasswordChanged) {
                if (oldPassword.text.toString()==savedPassword){
                    changePassword(User(oldPassword = savedPassword, newPassword = requiredPassword!!))
                } else{
                    oldPassword.error = "Old Password doesn't match"
                    progressBar.visibility=View.INVISIBLE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserDetailToSharedPref(email:String){
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

    private fun getCombinedOTP(otpDigits: Array<EditText>): String {
        val otpStringBuilder = StringBuilder()
        for (i in otpDigits.indices) {
            val otpDigit = otpDigits[i].text.toString()
            otpStringBuilder.append(otpDigit)
        }
        return otpStringBuilder.toString()
    }

    private fun executeFunctionAfterDelay(delayMillis: Long, function: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(function, delayMillis)
    }

    private fun startCountDownTimer(totalMillis: Long, resendOtp:TextView) {
        object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                resendOtp.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                resendOtp.text = "Resend" // Timer finished
            }
        }.start()
    }

}