package com.Jamie.juicewrldmusicplayer

import android.net.Uri


data class Song(
    val id:Int,
    val songName: String,
    val albumName: String,
    val albumImage: Long,
    val audioFile: String,
    val amountPlayed: Int
)


//object SongFactory {
//    fun createSong(songName: String, albumName:String, albumImage:Int, audioFile:String, amountPlayed:Int): Song = Song(1, songName, albumName, albumImage, audioFile, amountPlayed)
//}