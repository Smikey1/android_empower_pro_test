package com.hdd.empowerpro.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionHandler {
    private lateinit var applicationContext: Context
    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // checking for runtime permission
    fun checkRunTimePermission(context: Context) {
        applicationContext=context;
        if (!hasPermission()) {
            requestPermission()
        }
    }
    private fun hasPermission(): Boolean {
        var hasPermission = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = false
                break
            }
        }
        return hasPermission
    }

    // requesting permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(applicationContext as Activity, permissions, 1)
    }

}