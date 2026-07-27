package com.craiovadata.rfiplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlay
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
                startActionPlay(this, "http://asculta.rfi.ro:9128/live.mp3")
            }
            R.id.buttonItzyBitzy -> {
                startActionPlay(this, "http://live.itsybitsy.ro:8000/itsybitsy")
            }
            R.id.buttonFranceInter -> {
                startActionPlay(this, "http://icecast.radiofrance.fr/franceinter-hifi.aac")
                // "http://icecast.radiofrance.fr/franceinter-midfi.mp3" - data saver
            }
            R.id.buttonStop -> {
                startActionStop(this)
            }
        }
    }

}
