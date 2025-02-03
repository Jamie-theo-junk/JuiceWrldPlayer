package com.Jamie.juicewrldmusicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Jamie.juicewrldmusicplayer.databinding.ActivitySongPlayingBinding

class SongPlaying : AppCompatActivity(), SeekBarUpdateListener {

    private val TAG = "SongPlaying"
    private lateinit var binding: ActivitySongPlayingBinding
    private var mMediaPlayer: MediaPlayer? = null
    private var duration = 0
    private lateinit var seekBarReceiver: SeekBarReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View Binding
        binding = ActivitySongPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        seekBarReceiver = SeekBarReceiver(this) // Initialize BroadcastReceiver
        init()
        setupSeekBar()
    }

    private fun init() {
        val song = intent.getParcelableExtra<Song>("song")

        if (song != null) {
            Log.d(TAG, "init: Playing ${song.songName}")
        } else {
            Log.d(TAG, "init: No song data received")
        }

        song?.let {
            uiLogic(it) // 🔹 Call the UI logic method
            val mediaServiceIntent = Intent(this, MediaPlayerService::class.java).apply {
                action = MediaPlayerService.ACTION_PLAY
                putExtra("song", it)
            }
            startService(mediaServiceIntent)
        }
    }

    private fun setupSeekBar() {
        binding.songSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekTo(seekBar?.progress ?: 0)
            }
        })
    }

    private fun seekTo(position: Int) {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_SEEK
            putExtra("seekTo", position)
        }
        startService(intent)
    }

    // 🔹 BroadcastReceiver for SeekBar updates
    override fun onSeekBarUpdate(currentPosition: Int, duration: Int) {
        binding.songSeekBar.max = duration
        binding.songSeekBar.progress = currentPosition
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            seekBarReceiver,
            IntentFilter(MediaPlayerService.ACTION_UPDATE_SEEKBAR),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(seekBarReceiver)
    }

    // 🔹 UI Logic for updating song details
    private fun uiLogic(song: Song) {
        binding.songImage.setImageResource(song.albumImage)  // Set album image
        binding.songTitle.text = song.songName  // Set song title
        binding.songAlbum.text = song.albumName  // Set album name
    }
}
