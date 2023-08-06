package com.hdd.empowerpro.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    var email: String? = ""
    var password: String? = ""
    var token: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        retrieveUserDetails()
        SystemClock.sleep(2000)
    }

    private fun retrieveUserDetails() {
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        email = sharedPreferences.getString("email", "")
        password = sharedPreferences.getString("password", "")
        token = sharedPreferences.getString("token", "")
        withApiRetrofit()
    }

    private fun withApiRetrofit() {
        val currentAndroidId=Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID).toString()
        CoroutineScope(Dispatchers.IO).launch {
            val response = UserRepository().loginUser(User(email = email!!, password = password!!, androidId = currentAndroidId))
            if (response.success == true) {
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                ServiceBuilder.token = "$token"
                ServiceBuilder.user = response.data!!
                finish()
            } else {
                val androidId = getSharedPreferences("userAuth", MODE_PRIVATE).getString("androidId","")
                if (androidId==currentAndroidId) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }
            }
        }
    }

}