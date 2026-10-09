package com.petediano.peusic

import android.app.Application

class PeusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Future: initialise lightweight local services here.
        // No analytics, no tracking, no remote config.
    }
}
