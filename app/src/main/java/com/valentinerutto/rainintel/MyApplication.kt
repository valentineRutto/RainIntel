package com.valentinerutto.rainintel

import android.app.Application
import com.valentinerutto.rainintel.di.appModule
import com.valentinerutto.rainintel.di.databaseModule
import com.valentinerutto.rainintel.di.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {
    companion object {
        lateinit var INSTANCE: MyApplication
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        startKoin{
            androidLogger( level = Level.DEBUG)
            androidContext(this@MyApplication)

            modules(appModule,networkingModule, databaseModule)


        }

    }
}