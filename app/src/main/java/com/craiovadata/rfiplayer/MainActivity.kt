package com.craiovadata.rfiplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlay128
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlay48

class MainActivity : AppCompatActivity() {
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusTextView = findViewById(R.id.statusTextView)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlay48 -> {
                startActionPlay48(this)
                statusTextView.text = getString(R.string.text_48_kbps)
            }
            R.id.buttonPlay128 -> {
                startActionPlay128(this)
                statusTextView.text = getString(R.string.text_128_kbps)
            }
            R.id.buttonStop -> {
                val intent = Intent(this, AudioService::class.java)
                stopService(intent)
                statusTextView.text = null
            }
        }
    }

    override fun onStart() {
        super.onStart()
        statusTextView.text = AudioService.getCurrentContentText()?.let { getString(it) }
    }

}
