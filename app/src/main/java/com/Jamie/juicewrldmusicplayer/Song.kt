package com.Jamie.juicewrldmusicplayer

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



data class Song(
    val songName: String,
    val albumName: String,
    val albumImage: Int,
    val audioFile: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(songName)
        parcel.writeString(albumName)
        parcel.writeInt(albumImage)
        parcel.writeInt(audioFile)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song = Song(parcel)
        override fun newArray(size: Int): Array<Song?> = arrayOfNulls(size)
    }
}