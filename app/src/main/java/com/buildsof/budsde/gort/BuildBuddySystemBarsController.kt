package com.buildsof.budsde.gort

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat

class BuildBuddySystemBarsController(private val activity: Activity) {

    private val buildBuddyWindow = activity.window
    private val buildBuddyDecorView = buildBuddyWindow.decorView

    fun buildBuddySetupSystemBars() {
        val isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            buildBuddySetupForApi30Plus(isLandscape)
        } else {
            buildBuddySetupForApi24To29(isLandscape)
        }
    }

    private fun buildBuddySetupForApi30Plus(isLandscape: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(buildBuddyWindow, false)

            val insetsController = buildBuddyWindow.insetsController ?: return

            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isLandscape) {
                insetsController.hide(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
                )
            } else {
                insetsController.hide(WindowInsets.Type.navigationBars())
                insetsController.show(WindowInsets.Type.statusBars())
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun buildBuddySetupForApi24To29(isLandscape: Boolean) {
        var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        if (isLandscape) {
            flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            buildBuddyWindow.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        buildBuddyDecorView.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildBuddyWindow.navigationBarColor = android.graphics.Color.TRANSPARENT
            // Светлые иконки навигации (если нужно)
            buildBuddyDecorView.systemUiVisibility = flags and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }


}

fun Activity.buildBuddySetupSystemBars() {
    BuildBuddySystemBarsController(this).buildBuddySetupSystemBars()
}
