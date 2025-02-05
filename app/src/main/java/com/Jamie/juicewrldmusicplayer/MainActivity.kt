package com.Jamie.juicewrldmusicplayer

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Jamie.juicewrldmusicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playlistButton:RelativeLayout
    private lateinit var homeButton:RelativeLayout
    private lateinit var listButton:RelativeLayout
    private lateinit var dbHelper: DbHelper
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

}