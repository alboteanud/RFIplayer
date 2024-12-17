package com.craiovadata.rfiplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlay128
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlayFranceInter
import com.craiovadata.rfiplayer.AudioService.Companion.startActionStop

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (
            Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), /* requestCode= */ 0)
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlay128 -> {
                startActionPlay128(this)
            }
            R.id.buttonFranceInter -> {
                startActionPlayFranceInter(this)
            }
            R.id.buttonStop -> {
                startActionStop(this)
            }
        }
    }
}
