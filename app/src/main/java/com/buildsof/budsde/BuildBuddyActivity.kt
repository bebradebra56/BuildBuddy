package com.buildsof.budsde

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.buildsof.budsde.gort.BuildBuddyGlobalLayoutUtil
import com.buildsof.budsde.gort.buildBuddySetupSystemBars
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication
import com.buildsof.budsde.gort.presentation.pushhandler.BuildBuddyPushHandler
import org.koin.android.ext.android.inject

class BuildBuddyActivity : AppCompatActivity() {

    private val buildBuddyPushHandler by inject<BuildBuddyPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildBuddySetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_build_buddy)

        val buildBuddyRootView = findViewById<View>(android.R.id.content)
        BuildBuddyGlobalLayoutUtil().buildBuddyAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(buildBuddyRootView) { buildBuddyView, buildBuddyInsets ->
            val buildBuddySystemBars = buildBuddyInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val buildBuddyDisplayCutout = buildBuddyInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val buildBuddyIme = buildBuddyInsets.getInsets(WindowInsetsCompat.Type.ime())


            val buildBuddyTopPadding = maxOf(buildBuddySystemBars.top, buildBuddyDisplayCutout.top)
            val buildBuddyLeftPadding = maxOf(buildBuddySystemBars.left, buildBuddyDisplayCutout.left)
            val buildBuddyRightPadding = maxOf(buildBuddySystemBars.right, buildBuddyDisplayCutout.right)
            window.setSoftInputMode(BuildBuddyApplication.buildBuddyInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "ADJUST PUN")
                val buildBuddyBottomInset = maxOf(buildBuddySystemBars.bottom, buildBuddyDisplayCutout.bottom)

                buildBuddyView.setPadding(buildBuddyLeftPadding, buildBuddyTopPadding, buildBuddyRightPadding, 0)

                buildBuddyView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = buildBuddyBottomInset
                }
            } else {
                Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "ADJUST RESIZE")

                val buildBuddyBottomInset = maxOf(buildBuddySystemBars.bottom, buildBuddyDisplayCutout.bottom, buildBuddyIme.bottom)

                buildBuddyView.setPadding(buildBuddyLeftPadding, buildBuddyTopPadding, buildBuddyRightPadding, 0)

                buildBuddyView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = buildBuddyBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Activity onCreate()")
        buildBuddyPushHandler.buildBuddyHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            buildBuddySetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        buildBuddySetupSystemBars()
    }
}