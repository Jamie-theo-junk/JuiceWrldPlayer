package com.Jamie.juicewrldmusicplayer

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException

class MediaPlayerService: Service() {
     lateinit var mediaPlayer: MediaPlayer
    private val binder = LocalBinder()

    inner class LocalBinder: Binder(){
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer(){
        mediaPlayer = MediaPlayer().apply{
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    fun playMusic(song:Song){
        try{
//            mediaPlayer.apply {
//                setDataSource(path)
//                prepare()
//                start()
//            }
            mediaPlayer.apply {
                Log.d(TAG, "playMusic: playing ${song.audioFile}")

                setDataSource(song.audioFile)
                prepare()
                start()
            }
        }
        catch(error:IOException){
            Log.e(TAG, "playMusic: Error ", error)
        }
    }

    fun resumeMusic(){
        mediaPlayer.start()
    }

    fun pauseMusic(){
        mediaPlayer.pause()
    }

    fun stopMusic(){
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }

    companion object{
        private const val TAG = "MediaPlayerService"
    }

}