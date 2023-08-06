package com.hdd.empowerpro.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.domain.adapter.CustomSpinnerAdapter
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SignUpActivity : AppCompatActivity() {
    //Initialize
    private lateinit var et_sign_up_full_name: EditText
    private lateinit var et_sign_up_email: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var et_sign_up_password: EditText
    private lateinit var et_sign_up_confirm_password: EditText
    private lateinit var btnSignup: Button
    private lateinit var pb_sign_up: ProgressBar
    private lateinit var spinnerCountryCodes: Spinner

    val fullPhoneNumber = hashMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        et_sign_up_full_name = findViewById(R.id.et_sign_up_full_name)
        et_sign_up_email = findViewById(R.id.et_sign_up_email)
        et_sign_up_password = findViewById(R.id.et_sign_up_password)
        et_sign_up_confirm_password = findViewById(R.id.et_sign_up_confirm_password)
        btnSignup = findViewById(R.id.btnSignup)
        pb_sign_up = findViewById(R.id.pb_sign_up)
        spinnerCountryCodes = findViewById(R.id.spinnerCountryCodes)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)

        // validation Layer
        et_sign_up_full_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        et_sign_up_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        et_sign_up_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        et_sign_up_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })


        val countryCodesMap = getHashMapOfCountryCode()
        // Create custom ArrayAdapter with the countryCodesMap keys (country codes)
        val spinnerAdapter = CustomSpinnerAdapter(this@SignUpActivity, R.layout.spinner_dropdown_item, countryCodesMap)
        // Apply the adapter to the spinner
        spinnerCountryCodes.adapter = spinnerAdapter

            // Set an item selected listener to display only the country code after selection
        spinnerCountryCodes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val countryCode = spinnerCountryCodes.selectedItem.toString()
                val tvCountryCodeDropdown: TextView = view?.findViewById(R.id.tvCountryCodeDropdown) ?: return
                tvCountryCodeDropdown.text = countryCode
                val phoneNumber = etPhoneNumber.text.toString()
                fullPhoneNumber["countryCode"]=countryCode
                fullPhoneNumber["phone"]=phoneNumber
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected
            }
        }


        btnSignup.setOnClickListener {
            checkForEmailAndPassword()
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun checkForEmailAndPassword() {
        if (Patterns.EMAIL_ADDRESS.matcher(et_sign_up_email.text).matches()) {
            if (et_sign_up_password.text.toString() == et_sign_up_confirm_password.text.toString()) {
                pb_sign_up.isVisible = true
                btnSignup.isVisible = false
                btnSignup.setTextColor(R.color.textDisabledColor)
                signUpWithEmailAndPassword()
            } else {
                et_sign_up_confirm_password.error = "Password doesn't match"

            }
        } else {
            et_sign_up_email.error = "Please enter a valid email address"
            et_sign_up_email.requestFocus()
            return
        }
    }

    private fun signUpWithEmailAndPassword() {
        val user = User(
            fullname = et_sign_up_full_name.text.toString(),
            email = et_sign_up_email.text.toString(),
            newPassword = et_sign_up_password.text.toString(),
            countryCode = fullPhoneNumber["countryCode"],
            phone = fullPhoneNumber["phone"]
        )

        // signup with Api-Retrofit
        withApiRetrofit(user)
    }

    private fun withApiRetrofit(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.registerUser(user)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignUpActivity,"Successfully Registered",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    pb_sign_up.isVisible=false
                    btnSignup.isVisible=true
                    Toast.makeText(this@SignUpActivity, "Error $ex", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

// input check
    @SuppressLint("ResourceAsColor")
    private fun checkInputProvidedByUser() {
        if (!TextUtils.isEmpty(et_sign_up_email.text)) {
            if (!TextUtils.isEmpty(et_sign_up_full_name.text)) {
                if (!TextUtils.isEmpty(et_sign_up_password.text) && et_sign_up_password.length() >= 5) {
                    if (!TextUtils.isEmpty(et_sign_up_confirm_password.text) && et_sign_up_password.length() >= 5) {
                        btnSignup.isEnabled = true
                        btnSignup.setBackgroundColor(application.resources.getColor(R.color.buttonColor))
                        btnSignup.setTextColor(application.resources.getColor(R.color.white))
                        pb_sign_up.isEnabled = true
                    } else {
                        btnSignup.isEnabled = false
                        btnSignup.setTextColor(R.color.textDisabledColor)
                    }
                } else {
                    btnSignup.isEnabled = false
                    btnSignup.setTextColor(R.color.textDisabledColor)
                }
            } else {
                btnSignup.isEnabled = false
                btnSignup.setTextColor(R.color.textDisabledColor)
            }
        } else {
            btnSignup.isEnabled = false
            btnSignup.setTextColor(R.color.textDisabledColor)
        }
    }

    fun loginActivity(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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

}



