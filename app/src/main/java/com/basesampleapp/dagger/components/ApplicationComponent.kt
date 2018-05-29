package com.basesampleapp.dagger.components

import android.content.Context
import com.basesampleapp.dagger.modules.ApplicationModule
import com.basesampleapp.helper.KeyStoreHelper
import com.basesampleapp.helper.SharedPrefsHelper
import dagger.Component

@Component(modules = [(ApplicationModule::class)])
interface ApplicationComponent {

    companion object Holder {

        lateinit var instance: ApplicationComponent

        fun init(context: Context) {
            instance = DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(context))
                    .build()
        }
    }

    fun context(): Context
    fun getSharedPrefsHelper(): SharedPrefsHelper
    fun getKeyStoreHelper(): KeyStoreHelper
}