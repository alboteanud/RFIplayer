package com.craiovadata.rfiplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.MyService.Companion.startActionPlay
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlay48 -> {
                val playHQ = false
                startActionPlay(this, playHQ)
                updateUI(playHQ)
            }
            R.id.buttonPlay128 -> {
                val playHQ = true
                startActionPlay(this, playHQ)
                updateUI(playHQ)
            }
            R.id.buttonStop -> {
                val intent = Intent(this, MyService::class.java)
                stopService(intent)
                updateUI(null)
            }
        }
    }

    private fun updateUI(playHQ: Boolean?) {
        var statusText = "_"
        if (playHQ == null) {
            statusText = "_"
        } else if (!playHQ) {
            statusText = getString(R.string.text_48_kbps)
        } else if (playHQ) {
            statusText = getString(R.string.text_128_kbps)
        }
        statusTextView.text = statusText
    }

}
