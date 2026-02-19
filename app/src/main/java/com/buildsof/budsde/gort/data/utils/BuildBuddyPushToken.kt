package com.buildsof.budsde.gort.data.utils

import android.util.Log
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class BuildBuddyPushToken {

    suspend fun buildBuddyGetToken(
        buildBuddyMaxAttempts: Int = 3,
        buildBuddyDelayMs: Long = 1500
    ): String {

        repeat(buildBuddyMaxAttempts - 1) {
            try {
                val buildBuddyToken = FirebaseMessaging.getInstance().token.await()
                return buildBuddyToken
            } catch (e: Exception) {
                Log.e(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(buildBuddyDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}