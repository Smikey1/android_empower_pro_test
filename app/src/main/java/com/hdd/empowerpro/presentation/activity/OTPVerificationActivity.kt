package com.hdd.empowerpro.presentation.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hdd.empowerpro.R

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var otpDigits: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        // Initialize the array to hold references to the OTP digit EditText fields
        otpDigits = arrayOf(
            findViewById(R.id.otpDigit1),
            findViewById(R.id.otpDigit2),
            findViewById(R.id.otpDigit3),
            findViewById(R.id.otpDigit4),
            findViewById(R.id.otpDigit5),
            findViewById(R.id.otpDigit6)
        )

        // Set up TextWatchers to automatically move focus to the next EditText field
        // when a digit is entered in the current one
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

        val btnVerify: Button = findViewById(R.id.btnVerify)
        btnVerify.setOnClickListener {
            val enteredOTP = getCombinedOTP(otpDigits)
            // Replace the code below with your OTP verification logic
            if (enteredOTP == "123456") {
                showToast("OTP Verified!")
            } else {
                showToast("Invalid OTP! Please try again.")
            }
        }
    }

    private fun getCombinedOTP(otpDigits: Array<EditText>): String {
        val otpStringBuilder = StringBuilder()
        for (i in otpDigits.indices) {
            val otpDigit = otpDigits[i].text.toString()
            otpStringBuilder.append(otpDigit)
        }
        return otpStringBuilder.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
