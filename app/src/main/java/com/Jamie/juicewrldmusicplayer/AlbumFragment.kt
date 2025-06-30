package com.Jamie.juicewrldmusicplayer

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Jamie.juicewrldmusicplayer.databinding.FragmentAlbumBinding
import com.bumptech.glide.Glide

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var albumCover: ConstraintLayout
    private lateinit var albumTitle:TextView
    private lateinit var albumSongsRecycler: RecyclerView

    private lateinit var dbHelper: DbHelper

    private lateinit var adapter:ListRecyclerAdapter

    private var albumId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        albumId = arguments?.getInt("albumId")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlbumBinding.inflate(inflater,container, false)
        init()
        return binding.root
    }

    private fun init(){
        dbHelper = DbHelper(this.requireContext())
        albumCover = binding.albumImageCover
        albumTitle = binding.albumTxt
        albumId?.let {
            // Use it to fetch songs or update UI
            Log.d("AlbumListFragment", "Opened album with ID: $it")
            var albums = dbHelper.getAllAlbums()

            albums.forEach {
                Log.d(TAG, "Checking album: dbId=${it.id}, targetId=$albumId")
            }
            val albumSelected = albums.firstOrNull { it.id == albumId  }
            if (albumSelected == null) {
                Log.e("AlbumFragment", "No album found with ID: $id")
                
                return@let
            }
            Log.d(TAG, "init: albumSelected id is ${albumSelected.id}")

            val imageUri=convertUr(albumSelected.albumImage)
            albumTitle.text = albumSelected.albumName
            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        // Set background (convert Bitmap to Drawable)
                        albumCover.background = BitmapDrawable(resources, resource)

                        // Extracts the dominant color
                        Palette.from(resource).generate { palette ->
                            palette?.let {
                                val dominantColor = it.getDominantColor(Color.BLACK)
                                if (isColorDark(dominantColor)) {
                                    albumTitle.setTextColor(Color.WHITE)
                                } else {
                                    albumTitle.setTextColor(Color.BLACK)
                                }
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        albumCover.background = placeholder
                    }

                })
            albumSongsRecycler = binding.albumSongsRecycler
            albumSongsRecycler.layoutManager = LinearLayoutManager(requireContext())

            val songs = dbHelper.getAllSongs()
            val albumSongs = songs.filter { it.id in albumSelected.songIds }


            adapter = ListRecyclerAdapter(this.requireContext(),albumSongs )
            albumSongsRecycler.adapter = adapter

        }
    }
    private fun convertUr(longImage: Long):Uri{
        val albumUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            longImage
        )
        return albumUri
    }
    fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }


    companion object {
        private const val TAG = "AlbumFragment"
    }
}