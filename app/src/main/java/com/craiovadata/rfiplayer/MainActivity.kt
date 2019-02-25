package com.craiovadata.rfiplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.MyService.Companion.startActionPlay
import com.craiovadata.rfiplayer.MyService.Companion.startActionStop
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab_play.setOnClickListener { view ->

            startActionPlay(this)
        }

        fab_stop.setOnClickListener { view ->

            startActionStop(this)
        }


    }


    companion object {
        val TAG = "MainActivity"
    }

}
