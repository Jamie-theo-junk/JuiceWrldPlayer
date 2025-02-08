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

        private const val TABLE_NAME = "juice"
        private const val ID_COL = "id"
        private const val SONG_NAME_COL = "name"
        private const val ALBUM_COL = "album"
        private const val ALBUM_IMAGE_COL = "album_image"
        private const val AUDIO_FILE_COL = "audio_file"
        private const val AMOUNT_PLAYED = "amount_played"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$SONG_NAME_COL TEXT NOT NULL," +
                "$ALBUM_COL INTEGER NOT NULL," +
                "$ALBUM_IMAGE_COL INTEGER NOT NULL," +
                "$AUDIO_FILE_COL TEXT NOT NULL," +
                "$AMOUNT_PLAYED INTEGER NOT NULL DEFAULT 0)")

        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
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

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }


    fun getAllSongs(): List<Song> {
        val songList = mutableListOf<Song>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

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

    fun deleteUser(id: Int): Boolean{
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$ID_COL = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun updateUser(song: Song): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME_COL, song.songName)
        contentValues.put(ALBUM_COL, song.albumName)
        contentValues.put(ALBUM_IMAGE_COL, song.albumImage)
        contentValues.put(AUDIO_FILE_COL, song.audioFile)
        contentValues.put(AMOUNT_PLAYED, song.amountPlayed)

        val result = db.update(
            TABLE_NAME,
            contentValues,
            "$ID_COL = ?",
            arrayOf(song.id.toString())
        )
        db.close()
        return result > 0
    }

    fun getSongById(songId: Int): Song? {
        val db = readableDatabase
        var song: Song? = null

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL = ?", arrayOf(songId.toString()))

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

}