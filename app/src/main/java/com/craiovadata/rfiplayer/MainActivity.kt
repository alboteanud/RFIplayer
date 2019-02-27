package com.craiovadata.rfiplayer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.rfiplayer.MyService.Companion.getSavedPlayMode
import com.craiovadata.rfiplayer.MyService.Companion.savePlayMode
import com.craiovadata.rfiplayer.MyService.Companion.startActionPlay
import com.craiovadata.rfiplayer.MyService.Companion.startActionTogglePlay
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var menuItemChangeMode: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            startActionTogglePlay(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuItemChangeMode = menu?.findItem(R.id.menu_play_mode)
        val lowModePlay = getSavedPlayMode(this)
        updateUI(lowModePlay)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_play_mode -> {
                var lowModePlay = getSavedPlayMode(this)
                lowModePlay = !lowModePlay
                savePlayMode(this, lowModePlay)
                startActionPlay(this)
                updateUI(lowModePlay)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI(lowModePlay: Boolean) {
        val txt: String
        if (lowModePlay) {
            txt = getString(R.string.text_48_kbps)
        } else {
            txt = getString(R.string.text_128_kbps)
        }
        menuItemChangeMode?.title = txt
    }

}
