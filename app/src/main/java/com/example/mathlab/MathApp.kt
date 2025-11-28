package com.example.mathlab

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MathApp: Application(){

    override fun onCreate() {
        super.onCreate()
    }

}