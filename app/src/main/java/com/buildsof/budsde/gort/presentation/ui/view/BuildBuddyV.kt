package com.buildsof.budsde.gort.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication
import com.buildsof.budsde.gort.presentation.ui.load.BuildBuddyLoadFragment
import org.koin.android.ext.android.inject

class BuildBuddyV : Fragment(){

    private lateinit var buildBuddyPhoto: Uri
    private var buildBuddyFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val buildBuddyTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        buildBuddyFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        buildBuddyFilePathFromChrome = null
    }

    private val buildBuddyTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            buildBuddyFilePathFromChrome?.onReceiveValue(arrayOf(buildBuddyPhoto))
            buildBuddyFilePathFromChrome = null
        } else {
            buildBuddyFilePathFromChrome?.onReceiveValue(null)
            buildBuddyFilePathFromChrome = null
        }
    }

    private val buildBuddyDataStore by activityViewModels<BuildBuddyDataStore>()


    private val buildBuddyViFun by inject<BuildBuddyViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (buildBuddyDataStore.buildBuddyView.canGoBack()) {
                        buildBuddyDataStore.buildBuddyView.goBack()
                        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "WebView can go back")
                    } else if (buildBuddyDataStore.buildBuddyViList.size > 1) {
                        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "WebView can`t go back")
                        buildBuddyDataStore.buildBuddyViList.removeAt(buildBuddyDataStore.buildBuddyViList.lastIndex)
                        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "WebView list size ${buildBuddyDataStore.buildBuddyViList.size}")
                        buildBuddyDataStore.buildBuddyView.destroy()
                        val previousWebView = buildBuddyDataStore.buildBuddyViList.last()
                        buildBuddyAttachWebViewToContainer(previousWebView)
                        buildBuddyDataStore.buildBuddyView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (buildBuddyDataStore.buildBuddyIsFirstCreate) {
            buildBuddyDataStore.buildBuddyIsFirstCreate = false
            buildBuddyDataStore.buildBuddyContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return buildBuddyDataStore.buildBuddyContainerView
        } else {
            return buildBuddyDataStore.buildBuddyContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "onViewCreated")
        if (buildBuddyDataStore.buildBuddyViList.isEmpty()) {
            buildBuddyDataStore.buildBuddyView = BuildBuddyVi(requireContext(), object :
                BuildBuddyCallBack {
                override fun buildBuddyHandleCreateWebWindowRequest(buildBuddyVi: BuildBuddyVi) {
                    buildBuddyDataStore.buildBuddyViList.add(buildBuddyVi)
                    Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "WebView list size = ${buildBuddyDataStore.buildBuddyViList.size}")
                    Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "CreateWebWindowRequest")
                    buildBuddyDataStore.buildBuddyView = buildBuddyVi
                    buildBuddyVi.buildBuddySetFileChooserHandler { callback ->
                        buildBuddyHandleFileChooser(callback)
                    }
                    buildBuddyAttachWebViewToContainer(buildBuddyVi)
                }

            }, buildBuddyWindow = requireActivity().window).apply {
                buildBuddySetFileChooserHandler { callback ->
                    buildBuddyHandleFileChooser(callback)
                }
            }
            buildBuddyDataStore.buildBuddyView.buildBuddyFLoad(arguments?.getString(
                BuildBuddyLoadFragment.BUILD_BUDDY_D) ?: "")
//            ejvview.fLoad("www.google.com")
            buildBuddyDataStore.buildBuddyViList.add(buildBuddyDataStore.buildBuddyView)
            buildBuddyAttachWebViewToContainer(buildBuddyDataStore.buildBuddyView)
        } else {
            buildBuddyDataStore.buildBuddyViList.forEach { webView ->
                webView.buildBuddySetFileChooserHandler { callback ->
                    buildBuddyHandleFileChooser(callback)
                }
            }
            buildBuddyDataStore.buildBuddyView = buildBuddyDataStore.buildBuddyViList.last()

            buildBuddyAttachWebViewToContainer(buildBuddyDataStore.buildBuddyView)
        }
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "WebView list size = ${buildBuddyDataStore.buildBuddyViList.size}")
    }

    private fun buildBuddyHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        buildBuddyFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Launching file picker")
                    buildBuddyTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Launching camera")
                    buildBuddyPhoto = buildBuddyViFun.buildBuddySavePhoto()
                    buildBuddyTakePhoto.launch(buildBuddyPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                buildBuddyFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun buildBuddyAttachWebViewToContainer(w: BuildBuddyVi) {
        buildBuddyDataStore.buildBuddyContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            buildBuddyDataStore.buildBuddyContainerView.removeAllViews()
            buildBuddyDataStore.buildBuddyContainerView.addView(w)
        }
    }


}