package com.craiovadata.rfiplayer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {


    var isLowPlayMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        isLowPlayMode = MyService.getSavedPlayMode(this)
        updateUI()
    }

    override fun onClick(v: View?) {
        when (v) {
            fab -> {
                MyService.startActionTogglePlay(this)
            }
            button_toggle_play_mode -> {
                isLowPlayMode = !isLowPlayMode
                MyService.savePlayMode(this, isLowPlayMode)
                MyService.startActionPlay(this)
                updateUI()
            }
        }
    }


    fun updateUI() {
        if (isLowPlayMode) {
            button_toggle_play_mode.text = "48 kbps"
        } else {
            button_toggle_play_mode.text = "128 kbps"
        }
    }

}
