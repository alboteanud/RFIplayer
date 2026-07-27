package com.craiovadata.rfiplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlayRFI
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlayFranceInter
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlayItzyBitzy
import com.craiovadata.rfiplayer.AudioService.Companion.startActionStop

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), /* requestCode= */ 0)
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlayRfi -> {
                startActionPlayRFI(this)
            }
            R.id.buttonItzyBitzy -> {
                startActionPlayItzyBitzy(this)
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
