package com.Jamie.juicewrldmusicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SeekBarReceiver() : BroadcastReceiver() {

        private var callback: ((Int, Int) -> Unit)? = null

        fun setCallback(callback: ((currentPosition: Int, duration: Int) -> Unit)?) {
            this.callback = callback
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            val currentPosition = intent?.getIntExtra("currentPosition", 0) ?: 0
            val duration = intent?.getIntExtra("duration", 0) ?: 0

            callback?.invoke(currentPosition, duration)
        }


    companion object {
        private const val TAG = "SeekBarReceiver"
    }
}

