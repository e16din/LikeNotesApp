package me.likenotesapp

import android.app.Application

class AppPlatform : Application() {
    override fun onCreate() {
        super.onCreate()

        main()
    }
}