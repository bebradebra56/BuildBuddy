package com.buildsof.budsde.gort.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication
import java.util.Locale

class BuildBuddySystemService(private val context: Context) {
    

    fun buildBuddyGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    fun buildBuddyGetLocale() : String {
        return  Locale.getDefault().language
    }

    fun buildBuddyIsOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

}