package com.Jamie.juicewrldmusicplayer

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
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
import com.Jamie.juicewrldmusicplayer.databinding.FragmentListBinding
import com.bumptech.glide.Glide

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var albumCover: ConstraintLayout
    private lateinit var albumTitle:TextView
    private lateinit var albumSongs: RecyclerView

    private lateinit var dbHelper: DbHelper

    private lateinit var adapter:ListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        albumCover = binding.albumImageCover
        albumTitle = binding.albumTxt

        val album = HomeFragment.randomAlbum
        val imageUri=convertUr(album!!.albumImage)
        albumTitle.text = album.albumName
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
        albumSongs = binding.albumSongsRecycler
        albumSongs.layoutManager = LinearLayoutManager(requireContext())
        dbHelper = DbHelper(this.requireContext())
//        val songs = dbHelper.getAllSongs()
//        adapter = ListRecyclerAdapter(this.requireContext(), )
//        songRecycler.adapter = adapter
        //TODO: FIX THIS UP I WANT A RECYCLER VIEW TO WORK

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