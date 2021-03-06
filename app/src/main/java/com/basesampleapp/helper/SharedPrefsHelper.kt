package com.basesampleapp.helper

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Created by Hiren
 */
class SharedPrefsHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object Constants {

    }

    /**
     * Put boolean in shared preference
     * @param key Key for boolean value
     * @param value value of boolean
     */
    operator fun set(key: String, value: Boolean) {
        sharedPreferences.edit()?.putBoolean(key, value)?.apply()
    }

    /**
     * Put String in shared preference
     * @param key Key for String value
     * @param value value of String
     */
    operator fun set(key: String, value: String) {
        sharedPreferences.edit()?.putString(key, value)?.apply()
    }

    /**
     * Put Int in shared preference
     * @param key Key for Int value
     * @param value value of Int
     */
    operator fun set(key: String, value: Int) {
        sharedPreferences.edit()?.putInt(key, value)?.apply()
    }

    /**
     * Get value for key in boolean format
     * @param key Key for boolean value
     * @param defaultValue If no value found for given key then return default value
     */
    operator fun get(key: String, defaultValue: Boolean): Boolean =
            sharedPreferences.getBoolean(key, defaultValue)

    /**
     * Get value for key in String format
     * @param key Key for String value
     * @param defaultValue If no value found for given key then return default value
     */
    operator fun get(key: String, defaultValue: String): String =
            sharedPreferences.getString(key, defaultValue)

    /**
     * Put long in shared preference
     * @param key Key for long value
     * @param value value of long
     */
    operator fun set(key: String, value: Long) {
        sharedPreferences.edit()?.putLong(key, value)?.apply()
    }

    /**
     * Get value for key in long format
     * @param key Key for long value
     * @param defaultValue If no value found for given key then return default value
     */
    operator fun get(key: String, defaultValue: Long): Long =
            sharedPreferences.getLong(key, defaultValue)

    /**
     * Get value for key in Int format
     * @param key Key for Int value
     * @param defaultValue If no value found for given key then return default value
     */
    operator fun get(key: String, defaultValue: Int): Int =
            sharedPreferences.getInt(key, defaultValue)

    /**
     * Clear SharedPreference
     */
    fun clear() {
        sharedPreferences.edit()?.clear()?.apply()
    }

    /**
     * Get the shared preference
     */
    fun getSharedPreference(): SharedPreferences = sharedPreferences
}