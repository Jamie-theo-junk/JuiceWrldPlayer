package com.Jamie.juicewrldmusicplayer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "juicewrld_database.db"
        private const val DATABASE_VERSION = 1

        private const val JUICE_TABLE_NAME = "juice"
        private const val ID_COL = "id"
        private const val SONG_NAME_COL = "name"
        private const val ALBUM_COL = "album"
        private const val ALBUM_IMAGE_COL = "album_image"
        private const val AUDIO_FILE_COL = "audio_file"
        private const val AMOUNT_PLAYED = "amount_played"

        private const val ALBUM_TABLE_NAME = "albums"
        private const val ALBUM_ID_COL = "id"
        private const val ALBUM_NAME_COL = "album_name"
        private const val ALBUM_SONGS_COL = "songs"

        private const val PLAYLIST_NAME="playlists"
        private const val PLAYLIST_ID_COL = "id"
        private const val PLAYLIST_NAME_COL ="playlist_names"
        private const val SONG_ID_COL="playlist_songs_id"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE $JUICE_TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$SONG_NAME_COL TEXT NOT NULL," +
                "$ALBUM_COL TEXT NOT NULL," +
                "$ALBUM_IMAGE_COL INTEGER NOT NULL," +
                "$AUDIO_FILE_COL TEXT NOT NULL," +
                "$AMOUNT_PLAYED INTEGER NOT NULL DEFAULT 0" +
                ")")

        val albumQuery = ("CREATE TABLE $ALBUM_TABLE_NAME (" +
                "$ALBUM_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ALBUM_NAME_COL TEXT NOT NULL," +
                "$ALBUM_IMAGE_COL INTEGER NOT NULL," +
                "$ALBUM_SONGS_COL TEXT NOT NULL" + // comma-separated song names
                ")")

        val playlistQuery = """
        CREATE TABLE $PLAYLIST_NAME (
            $PLAYLIST_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
            $PLAYLIST_NAME_COL TEXT NOT NULL
        )
    """.trimIndent()

        val playlistSongsQuery = """
        CREATE TABLE playlist_songs (
            $PLAYLIST_ID_COL INTEGER NOT NULL,
            $SONG_ID_COL INTEGER NOT NULL,
            FOREIGN KEY($PLAYLIST_ID_COL) REFERENCES $PLAYLIST_NAME($PLAYLIST_ID_COL),
            FOREIGN KEY($SONG_ID_COL) REFERENCES $JUICE_TABLE_NAME($ID_COL)
        )
    """.trimIndent()

        db?.execSQL(playlistQuery)
        db?.execSQL(playlistSongsQuery)
        db?.execSQL(query)
        db?.execSQL(albumQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $JUICE_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $ALBUM_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $PLAYLIST_NAME")
        onCreate(db)
    }

    fun addSong(song: Song): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME_COL, song.songName)
        contentValues.put(ALBUM_COL, song.albumName)
        contentValues.put(ALBUM_IMAGE_COL, song.albumImage)
        contentValues.put(AUDIO_FILE_COL, song.audioFile)
        contentValues.put(AMOUNT_PLAYED, song.amountPlayed)

        val result = db.insert(JUICE_TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }


    fun getAllSongs(): List<Song> {
        val songList = mutableListOf<Song>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $JUICE_TABLE_NAME", null)

        with(cursor) {
            while (moveToNext()) {
                val idIndex = getColumnIndex(ID_COL)
                val songNameIndex = getColumnIndex(SONG_NAME_COL)
                val albumIndex = getColumnIndex(ALBUM_COL)
                val albumImageIndex = getColumnIndex(ALBUM_IMAGE_COL)
                val audioFileIndex = getColumnIndex(AUDIO_FILE_COL)
                val amountPlayedIndex = getColumnIndex(AMOUNT_PLAYED)

                // Ensure all column indices are valid before accessing data
                if (idIndex >= 0 && songNameIndex >= 0 && albumIndex >= 0 &&
                    albumImageIndex >= 0 && audioFileIndex >= 0 && amountPlayedIndex >= 0
                ) {
                    songList.add(
                        Song(
                            getInt(idIndex),
                            getString(songNameIndex),
                            getString(albumIndex),
                            getLong(albumImageIndex),
                            getString(audioFileIndex),
                            getInt(amountPlayedIndex)
                        )
                    )
                } else {
                    Log.e("DatabaseError", "One or more column indices not found in getAllSongs()")
                }
            }
        }

        cursor.close()
        db.close()
        return songList
    }

    fun getMostPlayedSong(): Song? {
        val db = readableDatabase
        var song: Song? = null

        val cursor = db.rawQuery(
            "SELECT * FROM $JUICE_TABLE_NAME ORDER BY $AMOUNT_PLAYED DESC LIMIT 1",
            null
        )

        if (cursor.moveToFirst()) {
            song = Song(
                id = cursor.getString(cursor.getColumnIndexOrThrow(ID_COL)).toInt(),
                songName = cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME_COL)),
                albumName = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_COL)),
                albumImage = cursor.getLong(cursor.getColumnIndexOrThrow(ALBUM_IMAGE_COL)),
                audioFile = cursor.getString(cursor.getColumnIndexOrThrow(AUDIO_FILE_COL)),
                amountPlayed = cursor.getInt(cursor.getColumnIndexOrThrow(AMOUNT_PLAYED))
            )
        }

        cursor.close()
        db.close()

        return song
    }

    fun getSongById(songId: Int): Song? {
        val db = readableDatabase
        var song: Song? = null

        val cursor = db.rawQuery("SELECT * FROM $JUICE_TABLE_NAME WHERE $ID_COL = ?", arrayOf(songId.toString()))

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(ID_COL)
            val songNameIndex = cursor.getColumnIndex(SONG_NAME_COL)
            val albumIndex = cursor.getColumnIndex(ALBUM_COL)
            val albumImageIndex = cursor.getColumnIndex(ALBUM_IMAGE_COL)
            val audioFileIndex = cursor.getColumnIndex(AUDIO_FILE_COL)
            val amountPlayedIndex = cursor.getColumnIndex(AMOUNT_PLAYED)

            // Ensure indices are valid before fetching data
            if (idIndex >= 0 && songNameIndex >= 0 && albumIndex >= 0 &&
                albumImageIndex >= 0 && audioFileIndex >= 0 && amountPlayedIndex >= 0
            ) {
                song = Song(
                    id = cursor.getInt(idIndex),
                    songName = cursor.getString(songNameIndex),
                    albumName = cursor.getString(albumIndex),
                    albumImage = cursor.getLong(albumImageIndex),
                    audioFile = cursor.getString(audioFileIndex),
                    amountPlayed = cursor.getInt(amountPlayedIndex)
                )
            }
        }

        cursor.close()
        db.close()
        return song // Returns null if no song is found
    }



    fun generateAlbumTableFromSongs() {
        val allSongs = getAllSongs()
        val db = writableDatabase

        val grouped = allSongs.groupBy { it.albumName }

        for ((albumName, songs) in grouped) {
            if (songs.isNotEmpty()) {
                val albumImage = songs.first().albumImage
                val songIds = songs.joinToString(",") { it.id.toString() }

                // Check if album already exists
                val cursor = db.rawQuery(
                    "SELECT * FROM $ALBUM_TABLE_NAME WHERE $ALBUM_NAME_COL = ?",
                    arrayOf(albumName)
                )

                if (cursor.moveToFirst()) {
                    // Album exists — update it
                    val contentValues = ContentValues().apply {
                        put(ALBUM_IMAGE_COL, albumImage)
                        put(ALBUM_SONGS_COL, songIds)
                    }
                    db.update(
                        ALBUM_TABLE_NAME,
                        contentValues,
                        "$ALBUM_NAME_COL = ?",
                        arrayOf(albumName)
                    )
                } else {
                    // Album doesn't exist — insert it
                    val contentValues = ContentValues().apply {
                        put(ALBUM_NAME_COL, albumName)
                        put(ALBUM_IMAGE_COL, albumImage)
                        put(ALBUM_SONGS_COL, songIds)
                    }
                    db.insert(ALBUM_TABLE_NAME, null, contentValues)
                }

                cursor.close()
            }
        }

        db.close()
    }

    fun getAllAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $ALBUM_TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ALBUM_ID_COL))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_NAME_COL))
                val image = cursor.getLong(cursor.getColumnIndexOrThrow(ALBUM_IMAGE_COL))
                val songIds = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_SONGS_COL))
                    .split(",")
                    .mapNotNull { it.toIntOrNull() }

                albums.add(Album(id, name, image, songIds))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return albums
    }

    fun addPlaylist(playlist: Playlist): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PLAYLIST_NAME_COL, playlist.name)
        val result = db.insert(PLAYLIST_NAME, null, contentValues)
        db.close()
        return result != -1L
    }

    fun addSongToPlaylist(playlistId: Int, songId: Int): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(PLAYLIST_ID_COL, playlistId)
            put(SONG_ID_COL, songId)
        }
        val result = db.insert("playlist_songs", null, contentValues)
        db.close()
        return result != -1L
    }

    fun getSongsForPlaylist(playlistId: Int): List<Song> {
        val songList = mutableListOf<Song>()
        val db = readableDatabase
        val query = """
        SELECT s.* FROM $JUICE_TABLE_NAME s
        INNER JOIN playlist_songs ps ON s.$ID_COL = ps.$SONG_ID_COL
        WHERE ps.$PLAYLIST_ID_COL = ?
    """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(playlistId.toString()))

        while (cursor.moveToNext()) {
            songList.add(
                Song(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL)),
                    songName = cursor.getString(cursor.getColumnIndexOrThrow(SONG_NAME_COL)),
                    albumName = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_COL)),
                    albumImage = cursor.getLong(cursor.getColumnIndexOrThrow(ALBUM_IMAGE_COL)),
                    audioFile = cursor.getString(cursor.getColumnIndexOrThrow(AUDIO_FILE_COL)),
                    amountPlayed = cursor.getInt(cursor.getColumnIndexOrThrow(AMOUNT_PLAYED))
                )
            )
        }

        cursor.close()
        db.close()
        return songList
    }

    fun incrementPlayCount(songId: Int): Boolean {
        val db = writableDatabase
        val result = db.execSQL(
            "UPDATE $JUICE_TABLE_NAME SET $AMOUNT_PLAYED = $AMOUNT_PLAYED + 1 WHERE $ID_COL = ?",
            arrayOf(songId)
        )
        db.close()
        return true // or catch exceptions to return false
    }

}