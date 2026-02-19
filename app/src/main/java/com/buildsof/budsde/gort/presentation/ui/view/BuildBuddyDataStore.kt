package com.buildsof.budsde.gort.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class BuildBuddyDataStore : ViewModel(){
    val buildBuddyViList: MutableList<BuildBuddyVi> = mutableListOf()
    var buildBuddyIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var buildBuddyContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var buildBuddyView: BuildBuddyVi

}