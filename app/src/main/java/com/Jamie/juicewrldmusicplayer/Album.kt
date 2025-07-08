package com.Jamie.juicewrldmusicplayer

data class Album(
    val id: Int,
    val albumName: String,
    val albumImage: Long,
    val songIds: List<Int>
)