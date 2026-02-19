package com.buildsof.budsde.gort.data.shar

import android.content.Context
import androidx.core.content.edit

class BuildBuddySharedPreference(context: Context) {
    private val buildBuddyPrefs = context.getSharedPreferences("buildBuddySharedPrefsAb", Context.MODE_PRIVATE)

    var buildBuddySavedUrl: String
        get() = buildBuddyPrefs.getString(BUILD_BUDDY_SAVED_URL, "") ?: ""
        set(value) = buildBuddyPrefs.edit { putString(BUILD_BUDDY_SAVED_URL, value) }

    var buildBuddyExpired : Long
        get() = buildBuddyPrefs.getLong(BUILD_BUDDY_EXPIRED, 0L)
        set(value) = buildBuddyPrefs.edit { putLong(BUILD_BUDDY_EXPIRED, value) }

    var buildBuddyAppState: Int
        get() = buildBuddyPrefs.getInt(BUILD_BUDDY_APPLICATION_STATE, 0)
        set(value) = buildBuddyPrefs.edit { putInt(BUILD_BUDDY_APPLICATION_STATE, value) }

    var buildBuddyNotificationRequest: Long
        get() = buildBuddyPrefs.getLong(BUILD_BUDDY_NOTIFICAITON_REQUEST, 0L)
        set(value) = buildBuddyPrefs.edit { putLong(BUILD_BUDDY_NOTIFICAITON_REQUEST, value) }

    var buildBuddyNotificationState:Int
        get() = buildBuddyPrefs.getInt(BUILD_BUDDY_NOTIFICATION_STATE, 0)
        set(value) = buildBuddyPrefs.edit { putInt(BUILD_BUDDY_NOTIFICATION_STATE, value) }

    companion object {
        private const val BUILD_BUDDY_NOTIFICATION_STATE = "buildBuddyNotificationState"
        private const val BUILD_BUDDY_SAVED_URL = "buildBuddySavedUrl"
        private const val BUILD_BUDDY_EXPIRED = "buildBuddyExpired"
        private const val BUILD_BUDDY_APPLICATION_STATE = "buildBuddyApplicationState"
        private const val BUILD_BUDDY_NOTIFICAITON_REQUEST = "buildBuddyNotificationRequest"
    }
}