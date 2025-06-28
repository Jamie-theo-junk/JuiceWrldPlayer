package com.Jamie.juicewrldmusicplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException

class MediaPlayerService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var currentProgress = 0
    private val binder = LocalBinder()

    var mediaPlayer: MediaPlayer? = null
    var currentSong: Song? = null
        private set(value) {
            field = value
            _currentSongLiveData.postValue(value)
        }

    // LiveData for observers (like MainActivity)
    private val _currentSongLiveData = MutableLiveData<Song?>()
    val currentSongLiveData: LiveData<Song?> get() = _currentSongLiveData

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        initializeNotification()
        initializeMediaPlayer()
    }

    private fun initializeNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing Music")
            .setSmallIcon(R.drawable.check)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            notificationBuilder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
        return super.onStartCommand(intent, flags, startId)
    }

    fun playMusic(song: Song) {
        try {
            if (currentSong?.audioFile == song.audioFile && mediaPlayer?.isPlaying == true) {
                return
            }

            mediaPlayer?.apply {
                stop()
                reset()
                release()
            }

            initializeMediaPlayer()
            mediaPlayer?.apply {
                setDataSource(song.audioFile)
                prepare()
                seekTo(currentProgress)
                start()
            }

            currentSong = song

        } catch (error: IOException) {
            Log.e(TAG, "playMusic: Error ", error)
        }
    }

    fun resumeMusic() {
        mediaPlayer?.start()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        currentProgress = mediaPlayer?.currentPosition ?: 0
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        currentProgress = 0
        currentSong = null // Will also post null to LiveData
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopMusic()
    }

    companion object {
        private const val CHANNEL_ID = "music_player_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "MediaPlayerService"
    }
}