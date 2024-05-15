package com.craiovadata.rfiplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlay128

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlay128 -> {
                startActionPlay128(this)
            }
            R.id.buttonStop -> {
                val intent = Intent(this, AudioService::class.java)
                stopService(intent)
            }
        }
    }
}
