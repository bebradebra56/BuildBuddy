package com.buildsof.budsde.gort.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.buildsof.budsde.MainActivity
import com.buildsof.budsde.R
import com.buildsof.budsde.databinding.FragmentLoadBuildBuddyBinding
import com.buildsof.budsde.gort.data.shar.BuildBuddySharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BuildBuddyLoadFragment : Fragment(R.layout.fragment_load_build_buddy) {
    private lateinit var buildBuddyLoadBinding: FragmentLoadBuildBuddyBinding

    private val buildBuddyLoadViewModel by viewModel<BuildBuddyLoadViewModel>()

    private val buildBuddySharedPreference by inject<BuildBuddySharedPreference>()

    private var buildBuddyUrl = ""

    private val buildBuddyRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        buildBuddySharedPreference.buildBuddyNotificationState = 2
        buildBuddyNavigateToSuccess(buildBuddyUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildBuddyLoadBinding = FragmentLoadBuildBuddyBinding.bind(view)

        buildBuddyLoadBinding.buildBuddyGrandButton.setOnClickListener {
            val buildBuddyPermission = Manifest.permission.POST_NOTIFICATIONS
            buildBuddyRequestNotificationPermission.launch(buildBuddyPermission)
        }

        buildBuddyLoadBinding.buildBuddySkipButton.setOnClickListener {
            buildBuddySharedPreference.buildBuddyNotificationState = 1
            buildBuddySharedPreference.buildBuddyNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            buildBuddyNavigateToSuccess(buildBuddyUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                buildBuddyLoadViewModel.buildBuddyHomeScreenState.collect {
                    when (it) {
                        is BuildBuddyLoadViewModel.BuildBuddyHomeScreenState.BuildBuddyLoading -> {

                        }

                        is BuildBuddyLoadViewModel.BuildBuddyHomeScreenState.BuildBuddyError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is BuildBuddyLoadViewModel.BuildBuddyHomeScreenState.BuildBuddySuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val buildBuddyNotificationState = buildBuddySharedPreference.buildBuddyNotificationState
                                when (buildBuddyNotificationState) {
                                    0 -> {
                                        buildBuddyLoadBinding.buildBuddyNotiGroup.visibility = View.VISIBLE
                                        buildBuddyLoadBinding.buildBuddyLoadingGroup.visibility = View.GONE
                                        buildBuddyUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > buildBuddySharedPreference.buildBuddyNotificationRequest) {
                                            buildBuddyLoadBinding.buildBuddyNotiGroup.visibility = View.VISIBLE
                                            buildBuddyLoadBinding.buildBuddyLoadingGroup.visibility = View.GONE
                                            buildBuddyUrl = it.data
                                        } else {
                                            buildBuddyNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        buildBuddyNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                buildBuddyNavigateToSuccess(it.data)
                            }
                        }

                        BuildBuddyLoadViewModel.BuildBuddyHomeScreenState.BuildBuddyNotInternet -> {
                            buildBuddyLoadBinding.buildBuddyStateGroup.visibility = View.VISIBLE
                            buildBuddyLoadBinding.buildBuddyLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun buildBuddyNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_buildBuddyLoadFragment_to_buildBuddyV,
            bundleOf(BUILD_BUDDY_D to data)
        )
    }

    companion object {
        const val BUILD_BUDDY_D = "buildBuddyData"
    }
}