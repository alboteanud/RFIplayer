package com.craiovadata.rfiplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.MyService.Companion.PREF_KEY_PLAY_HQ
import com.craiovadata.rfiplayer.MyService.Companion.startActionPlay
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlay48 -> {
                getSharedPreferences("_", Context.MODE_PRIVATE).edit()
                    .putBoolean(PREF_KEY_PLAY_HQ, false).apply()
                startActionPlay(this)
                statusTextView.text = getString(R.string.text_48_kbps)
            }
            R.id.buttonPlay128 -> {
                getSharedPreferences("_", Context.MODE_PRIVATE).edit()
                    .putBoolean(PREF_KEY_PLAY_HQ, true).apply()
                startActionPlay(this)
                statusTextView.text = getString(R.string.text_128_kbps)
            }
            R.id.buttonStop -> {
                val intent = Intent(this, MyService::class.java)
                stopService(intent)
                statusTextView.text = null
            }
        }
    }

}
