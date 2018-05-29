package com.basesampleapp.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import com.basesampleapp.helper.KeyStoreHelper
import com.basesampleapp.helper.SharedPrefsHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Hiren
 */
@Module
@Singleton
open class ApplicationModule(private val context: Context) {

    @Provides
    open fun provideSharedPrefs(): SharedPreferences =
            context.getSharedPreferences("SP_BASE_APP", Context.MODE_PRIVATE)

    @Provides
    open fun provideKeyStoreHelper(sharedPrefsHelper: SharedPrefsHelper) =
            KeyStoreHelper(context, sharedPrefsHelper)

    @Provides
    fun provideApplicationContext(): Context = context
}