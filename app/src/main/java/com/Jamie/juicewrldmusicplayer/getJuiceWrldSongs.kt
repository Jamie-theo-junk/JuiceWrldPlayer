package com.Jamie.juicewrldmusicplayer

import android.content.Context
import android.provider.MediaStore
import android.util.Log

object getJuiceWrldSongs{

    fun listSongs(context: Context): List<Song>{
        val songList = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE
        )//TODO REMOVE THIS

        val selection = "${MediaStore.Audio.Media.ARTIST} Like ?"
        val selectionArgs = arrayOf("%Juice%")

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,null,null,MediaStore.Audio.Media.TITLE + " ASC"
//        projection,
//        selection,
//        selectionArgs,
//        MediaStore.Audio.Media.TITLE + " ASC"
        )?.use {cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val filePath = cursor.getString(dataColumn)
                val albumId = cursor.getLong(albumIdColumn)


                Log.d("MusicScanner", "Found: $title by $artist album is $album" )
                if (artist?.lowercase()?.contains("juice wrld") == true ||
                    title?.lowercase()?.contains("juice wrld") == true) {
                    // Add to list if artist is correctly detected
                    songList.add(
                        Song(
                            id = id.toInt(),
                            songName = title,
                            albumName = album ?: "Unknown Album",
                            albumImage = albumId,
                            audioFile = filePath, // Using file path hash as a unique ID
                            amountPlayed = 0,
                            playlists = "none"
                        )
                    )
                }
            }
        }
        return songList
    }
}