package com.Jamie.juicewrldmusicplayer

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.Jamie.juicewrldmusicplayer.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mostPlayedImage:ImageView
    private lateinit var mostPlayedSongName:TextView
    private lateinit var mostPlayedSongAlbum:TextView
    private lateinit var song:Song
    private lateinit var dbHelper: DbHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater,container, false)
        init()
        return binding.root

    }

    private fun init(){
        //initializing the most played card View items
        mostPlayedImage = binding.mostPlayedImage
        mostPlayedSongAlbum = binding.songAlbum
        mostPlayedSongName = binding.songName

        settingUpDbItems()

        binding.mostPlayedCard.setOnClickListener {
            val toSong = Intent(context, SongPlaying::class.java)
            Log.d(TAG, "onBindViewHolder: ${song.songName}")
            Log.d(TAG, "onBindViewHolder: ${song.albumName }")
            Log.d(TAG, "onBindViewHolder: ${song.audioFile}")
            toSong.putExtra("songId",song.id)
            startActivity(toSong)
        }
        binding.latestAlbum.setOnClickListener {
        val fragment = AlbumFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
        }
    }


    private fun settingUpDbItems(){
        dbHelper = DbHelper(this.requireContext())
        dbHelper.getMostPlayedSong()?.let { mostPlayedSong  ->// to get and display the most played song in the list
             song = mostPlayedSong

            var mostPlayedImageUri = convertUr(song.albumImage)

            Glide
                .with(this)
                .load(mostPlayedImageUri)
                .centerCrop()
                .into(mostPlayedImage)

            mostPlayedSongAlbum.text = song.albumName
            mostPlayedSongName.text = song.songName
        }?: run {
            Log.d("DBHelper", "No song found.")

        }
        dbHelper.generateAlbumTableFromSongs()
        val albums = dbHelper.getAllAlbums()
        if(randomAlbum==null) {
            randomAlbum = albums.get((0..albums.size).random())
        }
        val albumImageUri = convertUr(randomAlbum!!.albumImage)
        Glide
            .with(this.requireContext())
            .load(albumImageUri)
            .centerCrop()
            .into(binding.latestAlbum)

    }

    private fun convertUr(longImage: Long):Uri{
        val albumUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            longImage
        )
        return albumUri
    }

    companion object {
        private const val TAG = "HomeFragment"
        var randomAlbum: Album? = null
    }
}