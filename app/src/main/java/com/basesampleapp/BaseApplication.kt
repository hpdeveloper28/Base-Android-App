package com.basesampleapp

import android.app.Application
import com.basesampleapp.dagger.components.ApplicationComponent
import com.basesampleapp.dagger.components.MainComponent

/**
 * Created by MobiquityInc on 29/05/18.
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApplicationComponent.init(this)
        MainComponent.init(this, ApplicationComponent.instance)
    }
}