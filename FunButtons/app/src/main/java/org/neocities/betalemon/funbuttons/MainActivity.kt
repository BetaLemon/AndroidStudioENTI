package org.neocities.betalemon.funbuttons

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soundsRecyclerView.layoutManager = LinearLayoutManager(this)



        button.setOnClickListener {
            //mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this , R.raw.pingas)
            mediaPlayer?.start()
        }

        button2.setOnClickListener {
            //mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this , R.raw.roblox)
            mediaPlayer?.start()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
