package com.Jamie.juicewrldmusicplayer

import android.content.ComponentName
import android.content.ContentUris
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Jamie.juicewrldmusicplayer.databinding.ActivityMainBinding
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playlistButton:RelativeLayout
    private lateinit var homeButton:RelativeLayout
    private lateinit var listButton:RelativeLayout
    private lateinit var dbHelper: DbHelper
    private lateinit var currentSongCard: CardView//the card at the bottom of the layout that gets the current song

    private var mediaPlayerService: MediaPlayerService? = null
    private lateinit var runnable: Runnable
    private val handler = Handler(Looper.getMainLooper())

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            mediaPlayerService = binder.getService()

            Log.d(TAG, "Service connected")

            // Observe current song changes
            mediaPlayerService?.currentSongLiveData?.observe(this@MainActivity) { song ->
                updateCurrentSongCard(song)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service disconnected")
            mediaPlayerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        init()
        val serviceIntent = Intent(this, MediaPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }
    private fun init(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }

        dbHelper = DbHelper(this)
        settingUpDb()
        playlistButton = binding.playlistRel
        homeButton = binding.homeRel
        listButton = binding.listRel
        playlistButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout,PlaylistFragment())
                .commit()
        }
        homeButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout,HomeFragment())
                .commit()
        }
        listButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout,ListFragment())
                .commit()
        }

        //adding the fragment to the main activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout,HomeFragment())
            .commit()

        binding.playImage.setOnClickListener {
            mediaPlayerService?.mediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayerService!!.pauseMusic()

                    // Stop the runnable updates
                    handler.removeCallbacks(runnable)

                }
                 else {
                    mediaPlayerService!!.resumeMusic()

                }
            }
        }

    }

    private fun settingUpDb(){

        val songs = getJuiceWrldSongs.listSongs(this)//the list that is bringing the data from the internal storage
        Log.d(TAG, "settingUpDb: ${songs.size}")

        val dbSongs = dbHelper.getAllSongs()

        val existingSongIds = dbSongs.map { it.audioFile}.toSet()

        for (song in songs){
            Log.d(TAG, "settingUpDb: ${song.songName}")
            if(!existingSongIds.contains(song.audioFile)) {
                dbHelper.addSong(song)
            }
        }
        Log.d(TAG, "settingUpDb: amount of songs is ${songs.size} ")
    }
    companion object{
        private const val TAG = "MainActivity"
    }

    private fun updateCurrentSongCard(song: Song?) {
        currentSongCard = binding.currentSongCard
        currentSongCard.visibility = View.GONE


        if (song == null) {
            currentSongCard.visibility = View.GONE
        } else {
            currentSongCard.visibility = View.VISIBLE

            val albumUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                song.albumImage
            )

            Glide
                .with(this)
                .load(albumUri)
                .centerCrop()
                .into(binding.songBackgroundImage)

            binding.songNameTxt.text = song.songName

            try {
                val duration = mediaPlayerService?.mediaPlayer?.duration
                if (duration != null) {
                    val formattedTime = formatDuration(duration.toLong())
                    binding.maxTimeStampTxt.text = "/$formattedTime"
                } else {
                    binding.maxTimeStampTxt.text = "/00:00"
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                binding.maxTimeStampTxt.text = "/00:00"
            }

            // Define runnable with safe isPlaying check
            runnable = object : Runnable {
                override fun run() {
                    mediaPlayerService?.mediaPlayer?.let { mediaPlayer ->
                        try {
                            if (mediaPlayer.isPlaying) {
                                val currentFormattedTime = formatDuration(mediaPlayer.currentPosition.toLong())
                                binding.timeStampTxt.text = currentFormattedTime
                                handler.postDelayed(this, 1000)
                            }
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                            handler.removeCallbacks(this)
                        }
                    }
                }
            }

            // Start the timer
            handler.postDelayed(runnable, 1000)
        }
    }
    private fun formatDuration(duration: Long): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

}