package com.buildsof.budsde.gort

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication

class BuildBuddyGlobalLayoutUtil {

    private var buildBuddyMChildOfContent: View? = null
    private var buildBuddyUsableHeightPrevious = 0

    fun buildBuddyAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        buildBuddyMChildOfContent = content.getChildAt(0)

        buildBuddyMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val buildBuddyUsableHeightNow = buildBuddyComputeUsableHeight()
        if (buildBuddyUsableHeightNow != buildBuddyUsableHeightPrevious) {
            val buildBuddyUsableHeightSansKeyboard = buildBuddyMChildOfContent?.rootView?.height ?: 0
            val buildBuddyHeightDifference = buildBuddyUsableHeightSansKeyboard - buildBuddyUsableHeightNow

            if (buildBuddyHeightDifference > (buildBuddyUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(BuildBuddyApplication.buildBuddyInputMode)
            } else {
                activity.window.setSoftInputMode(BuildBuddyApplication.buildBuddyInputMode)
            }
//            mChildOfContent?.requestLayout()
            buildBuddyUsableHeightPrevious = buildBuddyUsableHeightNow
        }
    }

    private fun buildBuddyComputeUsableHeight(): Int {
        val r = Rect()
        buildBuddyMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}