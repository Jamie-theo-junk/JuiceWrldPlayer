package com.Jamie.juicewrldmusicplayer

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Jamie.juicewrldmusicplayer.databinding.ActivitySongPlayingBinding

class SongPlaying : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    //TODO fix the minor bugs keeps crashing when moving to other songs

    private val TAG = "SongPlaying"
    private lateinit var binding: ActivitySongPlayingBinding

    private lateinit var seekBar: SeekBar
    private lateinit var mediaPlayerService: MediaPlayerService
    private var serviceBound = false
    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            mediaPlayerService = binder.getService()
            serviceBound = true
            Log.d(TAG, "onServiceConnected: connected")
            init()
            updateSeekBar()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            Log.d(TAG, "onServiceConnected: nota connected")
        }
    }


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

        val intentMediaPlayerService = Intent(this, MediaPlayerService::class.java)
        bindService(intentMediaPlayerService, serviceConnection, Context.BIND_AUTO_CREATE)



    }

    private fun init() {
        seekBar = binding.songSeekBar
        seekBar.setOnSeekBarChangeListener(this)



        val song = intent.getParcelableExtra<Song>("song")



        if (song != null) {
            Log.d(TAG, "init: Playing ${song.songName}")
        } else {
            Log.d(TAG, "init: No song data received")
        }

        song?.let {
            uiLogic(it) // 🔹 Call the UI logic method
            mediaPlayerService.playMusic(it)
        }
    }

    private fun updateSeekBar(){
        if(!serviceBound || mediaPlayerService.mediaPlayer.duration == 0) return

        seekBar.max = mediaPlayerService.mediaPlayer.duration
        Log.d(TAG, "updateSeekBar: ${seekBar.max}")

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run(){
                if(mediaPlayerService.mediaPlayer.isPlaying){
                    seekBar.progress = mediaPlayerService.mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)

                }
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun uiLogic(song: Song) {
        binding.songImage.setImageResource(song.albumImage)  // Set album image
        binding.songTitle.text = song.songName  // Set song title
        binding.songAlbum.text = song.albumName  // Set album name
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser && serviceBound) {
            mediaPlayerService.mediaPlayer.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onStart() {
        super.onStart()
        Intent(this, MediaPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) unbindService(serviceConnection)
    }
}

