package com.petediano.peusic.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/** Minimal stub service so manifest reference resolves. Full MediaSessionService restored after first green APK. */
class PlaybackService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
