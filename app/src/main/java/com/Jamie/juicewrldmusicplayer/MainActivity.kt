package com.Jamie.juicewrldmusicplayer

import android.os.Bundle
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
}