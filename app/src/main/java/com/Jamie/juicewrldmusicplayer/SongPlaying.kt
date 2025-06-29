package com.Jamie.juicewrldmusicplayer

import android.app.Activity
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Jamie.juicewrldmusicplayer.databinding.ActivitySongPlayingBinding
class SongPlaying : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private val TAG = "SongPlaying"
    private lateinit var binding: ActivitySongPlayingBinding

    private lateinit var seekBar: SeekBar
    private lateinit var mediaPlayerService: MediaPlayerService
    private var serviceBound = false
    private lateinit var pauseImageButton: ImageView
    private lateinit var maxTimeTextView: TextView
    private lateinit var currentTimeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val serviceConnection = object : ServiceConnection {
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
            Log.d(TAG, "onServiceConnected: not connected")
        }
    }

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

        startForegroundService(Intent(this, MediaPlayerService::class.java))
        val intentMediaPlayerService = Intent(this, MediaPlayerService::class.java)
        bindService(intentMediaPlayerService, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        if (serviceBound) {
            mediaPlayerService.stopMusic()
            val songId = intent.getIntExtra("songId", 0)
            val dbHelper = DbHelper(this)
            val selectedSong = dbHelper.getSongById(songId)
            selectedSong?.let {
                uiLogic(it)
                mediaPlayerService.playMusic(it)
                updateSeekBar()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (serviceBound) {
            stopSeekBarUpdate()
        }
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            unbindService(serviceConnection)
            stopSeekBarUpdate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSeekBarUpdate()
    }

    private fun init() {
        val dbHelper = DbHelper(this)

        pauseImageButton = binding.pauseResumeButton
        var isPlaying = true
        pauseImageButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayerService.pauseMusic()
                isPlaying = false
                pauseImageButton.setImageResource(R.drawable.pause)
            } else {
                mediaPlayerService.resumeMusic()
                isPlaying = true
                pauseImageButton.setImageResource(R.drawable.play_circle_outline)
                updateSeekBar()
            }
        }

        maxTimeTextView = binding.maxDuration
        currentTimeTextView = binding.duration
        seekBar = binding.songSeekBar
        seekBar.setOnSeekBarChangeListener(this)

        val songId = intent.getIntExtra("songId", 0)
        val selectedSong = dbHelper.getSongById(songId)
        if (selectedSong != null) {
            Log.d(TAG, "init: Playing ${selectedSong.songName}")
            Log.d(TAG, "init: Playing ${selectedSong.audioFile}")
        } else {
            Log.d(TAG, "init: No song data received")
        }
        selectedSong?.let {
            uiLogic(it)
            dbHelper.incrementPlayCount(songId)
            mediaPlayerService.playMusic(it)
            val formattedTime = formatDuration(mediaPlayerService.mediaPlayer!!.duration.toLong())
            maxTimeTextView.text = formattedTime
        }
    }

    private fun formatDuration(duration: Long): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateSeekBar() {
        if (!serviceBound || mediaPlayerService.mediaPlayer!!.duration == 0) return

        seekBar.max = mediaPlayerService.mediaPlayer!!.duration
        Log.d(TAG, "updateSeekBar: ${seekBar.max}")

        runnable = object : Runnable {
            override fun run() {
                if (mediaPlayerService.mediaPlayer!!.isPlaying) {
                    seekBar.progress = mediaPlayerService.mediaPlayer!!.currentPosition
                    val currentFormattedTime = formatDuration(mediaPlayerService.mediaPlayer!!.currentPosition.toLong())
                    currentTimeTextView.text = currentFormattedTime
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(runnable)
    }

    private fun uiLogic(song: Song) {
        val albumUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            song.albumImage
        )
        binding.songImage.setImageURI(albumUri)
        binding.songTitle.text = song.songName
        binding.songAlbum.text = song.albumName
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser && serviceBound) {
            mediaPlayerService.mediaPlayer!!.seekTo(progress)
            updateSeekBar()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}
