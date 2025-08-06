package com.jawexerciser.app

import android.app.Application
import android.util.Log

class JawExerciserApplication : Application() {
    
    companion object {
        private const val TAG = "JawExerciserApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started successfully")
    }
}
