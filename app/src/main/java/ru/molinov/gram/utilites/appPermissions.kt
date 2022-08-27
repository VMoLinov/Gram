package ru.molinov.gram.utilites

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
const val WRITE_FILES = Manifest.permission.WRITE_EXTERNAL_STORAGE
const val PERMISSION_REQUEST = 200

@SuppressLint("ObsoleteSdkInt")
fun checkPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23
        && ContextCompat
            .checkSelfPermission(MAIN_ACTIVITY, permission) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(MAIN_ACTIVITY, arrayOf(permission), PERMISSION_REQUEST)
        false
    } else true
}
