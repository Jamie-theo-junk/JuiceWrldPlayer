package com.Jamie.juicewrldmusicplayer

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



data class Song(
    val id:Int,
    val songName: String,
    val albumName: String,
    val albumImage: Long,
    val audioFile: String,
    val amountPlayed: Int,
    val playlists:String
)