package com.Jamie.juicewrldmusicplayer

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AlbumListRecyclerAdapter (
    private val context: Context,
    private val albums: List<Album>,
    private val onAlbumClick: (Album) -> Unit
    ) : RecyclerView.Adapter<AlbumListRecyclerAdapter.AlbumViewHolder>(){

        private val TAG = "AlbumListRecyclerAdapter"
        class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val albumName: TextView = itemView.findViewById(R.id.album_name)
            val albumImage: ImageView = itemView.findViewById(R.id.album_image)
            val albumConstraint: ConstraintLayout = itemView.findViewById(R.id.album)
            fun bind(album: Album, context: Context, onAlbumClick: (Album) -> Unit) {
                albumName.text = album.albumName
                val albumUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    album.albumImage
                )

                Glide
                    .with(context)
                    .load(albumUri)
                    .centerCrop()
                    .into(albumImage)

                albumConstraint.setOnClickListener {
                    onAlbumClick(album)
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.album_card, parent, false)
            return AlbumViewHolder(view)
        }

        override fun getItemCount(): Int {
            return albums.size
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            val album = albums[position]
            holder.bind(album,context,onAlbumClick)
        }
    }
