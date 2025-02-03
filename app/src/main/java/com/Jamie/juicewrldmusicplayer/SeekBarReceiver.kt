package com.Jamie.juicewrldmusicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SeekBarReceiver(private val listener: SeekBarUpdateListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val currentPosition = it.getIntExtra("currentPosition", 0)
            val duration = it.getIntExtra("duration", 0)
            Log.d(TAG, "Received SeekBar Update: Position: $currentPosition, Duration: $duration")

            // Update SeekBar in UI via the listener
            listener.onSeekBarUpdate(currentPosition, duration)
        }
    }

    companion object {
        private const val TAG = "SeekBarReceiver"
    }
}

// Interface to update SeekBar in the Activity
interface SeekBarUpdateListener {
    fun onSeekBarUpdate(currentPosition: Int, duration: Int)
}
