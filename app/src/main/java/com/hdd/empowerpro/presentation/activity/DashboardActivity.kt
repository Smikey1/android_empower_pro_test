package com.hdd.empowerpro.presentation.activity

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings.Secure
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    // notification
    private val channelID = "loginChannel"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "EmpowerPro", "Notification Channel")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        executeFunctionAfterDelay(1500L) {
        retrieveUserDetails()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_bottom)
        navView.setupWithNavController(navController)
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)

        }

    }

    private fun displayNotification(message: String) {
        val notificationId = 24
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("EmpowerPro")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager?.notify(notificationId, notification)

    }


    private fun updateUserDevice() {
        val currentAndroidId = Secure.getString(this.contentResolver, Secure.ANDROID_ID).toString()
        val user = User(
            androidId = currentAndroidId
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                UserRepository().updateUserDevice(user)
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun retrieveUserDetails() {
        if(ServiceBuilder.loginCode==null){
            displayNotification("Login Successful on ${Build.MODEL}");
        } else {
            showOTPVerificationAlertDialog();
            executeFunctionAfterDelay(3000L) {
            displayNotification("${ServiceBuilder.loginCode} is your login verification code");
            }
        }
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

        val progressBar: ProgressBar = otpDialog.findViewById(R.id.pb_otp)
        val cancel: Button = otpDialog.findViewById(R.id.otp_cancel)
        val btnVerify: Button = otpDialog.findViewById(R.id.otp_verify)

        val resendOtp: TextView = otpDialog.findViewById(R.id.tvResendOTPText)

        var otpDigits: Array<EditText> = arrayOf(
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
            otpDialog.dismiss()
            startActivity(Intent(this, LoginActivity::class.java))
            val sharedPreferences = getSharedPreferences("userAuth", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()!!
            editor.putString("token", "")
            editor.putString("email", "")
            editor.putString("password", "")
            editor.apply()
            finish()
        }

        resendOtp.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRepository = UserRepository()
                    val response = userRepository.resendLoginOTP()
                    if (response.success == true) {
                        ServiceBuilder.loginCode = response.accessCode;
                        withContext(Dispatchers.Main) {
                            showToast("Otp Sent")
                            executeFunctionAfterDelay(3000L) {
                                displayNotification("${ServiceBuilder.loginCode} is your login verification code");
                            }
                            val threeMinutesInMillis: Long = 3 * 60 * 1000
                            startCountDownTimer(threeMinutesInMillis,resendOtp)
                            resendOtp.isEnabled=false
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@DashboardActivity,
                                    "Failed to resend code",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DashboardActivity,
                            "Failed to resend code",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }

        btnVerify.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            executeFunctionAfterDelay(3000L) {
                val enteredOTP = getCombinedOTP(otpDigits)
                // Replace the code below with your OTP verification logic
                if (enteredOTP == ServiceBuilder.loginCode!!) {
                    updateUserDevice()
                    progressBar.visibility = View.GONE
                    showToast("OTP Verified!")
                    otpDialog.dismiss();
                } else {
                    progressBar.visibility = View.GONE
                    showToast("Invalid OTP! Please try again.")
                }
            }
        }
        otpDialog.setCancelable(false)
        otpDialog.show()
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
                resendOtp.isEnabled=true
            }
        }.start()
    }

}