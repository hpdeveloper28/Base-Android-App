package com.basesampleapp.dagger.components

import android.content.Context
import com.basesampleapp.HomeActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [],
        dependencies = [(ApplicationComponent::class)])
interface MainComponent {

    companion object Factory {

        lateinit var instance: MainComponent

        fun init(context: Context, applicationComponent: ApplicationComponent) {
            instance = DaggerMainComponent.builder()
                    .applicationComponent(applicationComponent)
                    .build()
        }
    }

    fun inject(homeActivity: HomeActivity)
}
