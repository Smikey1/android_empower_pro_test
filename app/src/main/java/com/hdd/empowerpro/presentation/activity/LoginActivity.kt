package com.hdd.empowerpro.presentation.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.UserRepository
import com.hdd.empowerpro.utils.PermissionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    //Initialize
    private lateinit var et_sign_in_email: EditText
    private lateinit var et_sign_in_password: EditText
    private lateinit var tv_sign_in_forgot_password: TextView
    private lateinit var btnLogin: Button
    private lateinit var pb_sign_in: ProgressBar

    // notification
    private val channelID = "loginChannel"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ServiceBuilder.token!=null){
            displayNotification()
        }

        setContentView(R.layout.activity_login)
        et_sign_in_email = findViewById(R.id.et_sign_in_email)
        et_sign_in_password = findViewById(R.id.et_sign_in_password)
        btnLogin = findViewById(R.id.btnLogin)
        pb_sign_in = findViewById(R.id.pb_sign_in)
        tv_sign_in_forgot_password = findViewById(R.id.tv_sign_in_forgot_password)
        PermissionHandler.checkRunTimePermission(this)

        tv_sign_in_forgot_password.setOnClickListener {
            forgotPasswordActivity()
        }

        et_sign_in_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        et_sign_in_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        btnLogin.setOnClickListener {
            checkForEmailAndPassword()
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun checkForEmailAndPassword() {
        if (Patterns.EMAIL_ADDRESS.matcher(et_sign_in_email.text).matches()) {
            if (et_sign_in_password.length() >= 5) {
                pb_sign_in.isVisible = true
                btnLogin.isVisible = false
                btnLogin.setTextColor(R.color.textDisabledColor)
                logInWithEmailAndPassword()
            } else {
                et_sign_in_password.error = "Password doesn't match"

            }
        } else {
            et_sign_in_email.error = "Please enter a valid email address"
            et_sign_in_email.requestFocus()
            return
        }
    }

    private fun logInWithEmailAndPassword() {

        // login with Api-Retrofit
        withApiRetrofit()
    }

    private fun displayNotification() {
        val notificationId = 24
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("EmpowerPro")
            .setContentText("Login from new ${Build.DEVICE} device detected")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager?.notify(notificationId, notification)

    }

    private fun withApiRetrofit() {
        val currentAndroidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID).toString()
        val email = et_sign_in_email.text.toString()
        val password = et_sign_in_password.text.toString()
        // from retrofit-model
        val user = User(email = email, password = password, androidId = currentAndroidId )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.loginUser(user)

                if (response.success == true) {
                    ServiceBuilder.token = "Bearer ${response.accessToken!!}"
                    if(response.accessCode != null){
                    ServiceBuilder.loginCode = response.accessCode;
                    }
                    ServiceBuilder.uid = response.data!!._id
                    ServiceBuilder.user = response.data
                    withContext(Dispatchers.Main) {
                         addUserDetailInfo()
                        Toast.makeText(this@LoginActivity, "Successfully Login", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity,
                            "Invalid username or password",
                            Toast.LENGTH_SHORT).show()
                        pb_sign_in.isVisible = false
                        btnLogin.isVisible = true
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    pb_sign_in.isVisible = false
                    btnLogin.isVisible = true
                    Toast.makeText(this@LoginActivity,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun addUserDetailInfo() {
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", et_sign_in_email.text.toString())
        editor.putString("password", et_sign_in_password.text.toString())
        editor.putString("androidId", Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID).toString())
        editor.putString("token", ServiceBuilder.token!!)
        editor.apply()
    }

    // check user details
    @SuppressLint("ResourceAsColor")
    private fun checkInputProvidedByUser() {
        if (!TextUtils.isEmpty(et_sign_in_email.text)) {
            if (et_sign_in_password.length() >= 5) {
                btnLogin.isEnabled = true
                btnLogin.setBackgroundColor(application.resources.getColor(R.color.buttonColor))
                btnLogin.setTextColor(application.resources.getColor(R.color.white))
                pb_sign_in.isEnabled = true
            } else {
                btnLogin.isEnabled = false
                btnLogin.setTextColor(R.color.textDisabledColor)
            }
        } else {
            btnLogin.isEnabled = false
            btnLogin.setTextColor(R.color.textDisabledColor)
        }
    }

    fun signUpActivity(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun forgotPasswordActivity() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    //on back press trigger
    override fun onBackPressed() {
        showLogOutAlertDialog()
    }

    private fun showLogOutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Close App")
        builder.setIcon(R.drawable.exit)
        builder.setMessage("Are you sure to close app ?")
//        builder.setIcon(R.id.nav_sign_out)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            super.onBackPressed()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
            Toast.makeText(this, "clicked cancel", Toast.LENGTH_SHORT).show()
        }
        //performing negative action
        builder.setNegativeButton("No") { _, _ ->
            Toast.makeText(this, "clicked No", Toast.LENGTH_SHORT).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}