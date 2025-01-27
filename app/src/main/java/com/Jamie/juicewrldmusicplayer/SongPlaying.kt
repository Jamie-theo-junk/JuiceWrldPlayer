package com.Jamie.juicewrldmusicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Jamie.juicewrldmusicplayer.databinding.ActivitySongPlayingBinding

class SongPlaying : AppCompatActivity() {

    private  val TAG = "SongPlaying"

    private lateinit var binding: ActivitySongPlayingBinding
    private  var song:Song? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySongPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init(){

         song = intent.getParcelableExtra("songs")
        song?.let {
            Log.d(TAG, "init: started song")
            Log.d(TAG, "song name: ${it.songName}")
            val mediaPlayer = MediaPlayer.create(applicationContext, it.audioFile )
            mediaPlayer.start()
        }
        Log.d(TAG, "init: stopped")
    }
}