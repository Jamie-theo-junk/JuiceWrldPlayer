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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ListRecyclerAdapter(
    private val context: Context,
    private val songs: List<Song>
) : RecyclerView.Adapter<ListRecyclerAdapter.SongViewHolder>(){

    private val TAG = "ListRecyclerAdapter"
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songName: TextView = itemView.findViewById(R.id.song_name_txt)
        val image: ImageView = itemView.findViewById(R.id.song_image)
        val album: TextView = itemView.findViewById(R.id.album_txt)
        val relativeCard: RelativeLayout = itemView.findViewById(R.id.music_card_relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_card, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songName.text = song.songName
        holder.album.text = song.albumName
        val albumUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            song.albumImage
        )
        holder.image.setImageURI(albumUri)
        holder.relativeCard.setOnClickListener {
            val toSong = Intent(context, SongPlaying::class.java)
            Log.d(TAG, "onBindViewHolder: ${song.songName}")
            Log.d(TAG, "onBindViewHolder: ${song.albumName }")
            Log.d(TAG, "onBindViewHolder: ${song.audioFile}")
            toSong.putExtra("songId",song.id)
            context.startActivity(toSong)
        }

    }
}