package com.buildsof.budsde.gort.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication

class BuildBuddyPushHandler {
    fun buildBuddyHandlePush(extras: Bundle?) {
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map: MutableMap<String, String?> = HashMap()
            val ks = extras.keySet()
            val iterator: Iterator<String> = ks.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                map[key] = extras.getString(key)
            }
            Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Map from Push = $map")
            map.let {
                if (map.containsKey("url")) {
                    BuildBuddyApplication.BUILD_BUDDY_FB_LI = map["url"]
                    Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Push data no!")
        }
    }

}