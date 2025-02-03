package com.Jamie.juicewrldmusicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.os.Handler

class MediaPlayerService: Service(), MediaPlayer.OnPreparedListener {

    private var mMediaPlayer: MediaPlayer? = null
    private val handler = Handler()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            mMediaPlayer?.let {
                val intent = Intent(ACTION_UPDATE_SEEKBAR).apply {
                    putExtra("currentPosition", it.currentPosition)
                    putExtra("duration", it.duration)
                }
                sendBroadcast(intent)  // ✅ Send updates to SeekBarReceiver
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        Log.d("MediaPlayerService", "onPrepared: MediaPlayer is ready to play")
        mp.start()
        handler.post(updateSeekBarRunnable)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_PLAY -> {
                val song = intent.getParcelableExtra<Song>("song")
                song?.let {
                    mMediaPlayer?.release()
                    mMediaPlayer = MediaPlayer().apply {
                        val afd = applicationContext.resources.openRawResourceFd(it.audioFile)
                        setDataSource(afd?.fileDescriptor, afd?.startOffset ?: 0, afd?.length ?: 0)
                        afd?.close()
                        setOnPreparedListener(this@MediaPlayerService)
                        prepareAsync()
                    }
                }
            }
            ACTION_SEEK -> {
                val seekPosition = intent.getIntExtra("seekTo", 0)
                mMediaPlayer?.seekTo(seekPosition)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.apply {
            stop()
            release()
        }
        Log.d("MediaPlayerService", "Service Destroyed")
    }

    companion object {
        const val ACTION_PLAY = "com.example.action.PLAY"
        const val ACTION_SEEK = "com.example.action.SEEK"
        const val ACTION_UPDATE_SEEKBAR = "com.example.action.UPDATE_SEEKBAR"
    }
}
