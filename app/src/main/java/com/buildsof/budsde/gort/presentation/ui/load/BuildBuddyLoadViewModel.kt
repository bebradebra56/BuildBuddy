package com.buildsof.budsde.gort.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.gort.data.shar.BuildBuddySharedPreference
import com.buildsof.budsde.gort.data.utils.BuildBuddySystemService
import com.buildsof.budsde.gort.domain.usecases.BuildBuddyGetAllUseCase
import com.buildsof.budsde.gort.presentation.app.BuildBuddyAppsFlyerState
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuildBuddyLoadViewModel(
    private val buildBuddyGetAllUseCase: BuildBuddyGetAllUseCase,
    private val buildBuddySharedPreference: BuildBuddySharedPreference,
    private val buildBuddySystemService: BuildBuddySystemService
) : ViewModel() {

    private val _buildBuddyHomeScreenState: MutableStateFlow<BuildBuddyHomeScreenState> =
        MutableStateFlow(BuildBuddyHomeScreenState.BuildBuddyLoading)
    val buildBuddyHomeScreenState = _buildBuddyHomeScreenState.asStateFlow()

    private var buildBuddyGetApps = false


    init {
        viewModelScope.launch {
            when (buildBuddySharedPreference.buildBuddyAppState) {
                0 -> {
                    if (buildBuddySystemService.buildBuddyIsOnline()) {
                        BuildBuddyApplication.buildBuddyConversionFlow.collect {
                            when(it) {
                                BuildBuddyAppsFlyerState.BuildBuddyDefault -> {}
                                BuildBuddyAppsFlyerState.BuildBuddyError -> {
                                    buildBuddySharedPreference.buildBuddyAppState = 2
                                    _buildBuddyHomeScreenState.value =
                                        BuildBuddyHomeScreenState.BuildBuddyError
                                    buildBuddyGetApps = true
                                }
                                is BuildBuddyAppsFlyerState.BuildBuddySuccess -> {
                                    if (!buildBuddyGetApps) {
                                        buildBuddyGetData(it.buildBuddyData)
                                        buildBuddyGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _buildBuddyHomeScreenState.value =
                            BuildBuddyHomeScreenState.BuildBuddyNotInternet
                    }
                }
                1 -> {
                    if (buildBuddySystemService.buildBuddyIsOnline()) {
                        if (BuildBuddyApplication.BUILD_BUDDY_FB_LI != null) {
                            _buildBuddyHomeScreenState.value =
                                BuildBuddyHomeScreenState.BuildBuddySuccess(
                                    BuildBuddyApplication.BUILD_BUDDY_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > buildBuddySharedPreference.buildBuddyExpired) {
                            Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Current time more then expired, repeat request")
                            BuildBuddyApplication.buildBuddyConversionFlow.collect {
                                when(it) {
                                    BuildBuddyAppsFlyerState.BuildBuddyDefault -> {}
                                    BuildBuddyAppsFlyerState.BuildBuddyError -> {
                                        _buildBuddyHomeScreenState.value =
                                            BuildBuddyHomeScreenState.BuildBuddySuccess(
                                                buildBuddySharedPreference.buildBuddySavedUrl
                                            )
                                        buildBuddyGetApps = true
                                    }
                                    is BuildBuddyAppsFlyerState.BuildBuddySuccess -> {
                                        if (!buildBuddyGetApps) {
                                            buildBuddyGetData(it.buildBuddyData)
                                            buildBuddyGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Current time less then expired, use saved url")
                            _buildBuddyHomeScreenState.value =
                                BuildBuddyHomeScreenState.BuildBuddySuccess(
                                    buildBuddySharedPreference.buildBuddySavedUrl
                                )
                        }
                    } else {
                        _buildBuddyHomeScreenState.value =
                            BuildBuddyHomeScreenState.BuildBuddyNotInternet
                    }
                }
                2 -> {
                    _buildBuddyHomeScreenState.value =
                        BuildBuddyHomeScreenState.BuildBuddyError
                }
            }
        }
    }


    private suspend fun buildBuddyGetData(conversation: MutableMap<String, Any>?) {
        val buildBuddyData = buildBuddyGetAllUseCase.invoke(conversation)
        if (buildBuddySharedPreference.buildBuddyAppState == 0) {
            if (buildBuddyData == null) {
                buildBuddySharedPreference.buildBuddyAppState = 2
                _buildBuddyHomeScreenState.value =
                    BuildBuddyHomeScreenState.BuildBuddyError
            } else {
                buildBuddySharedPreference.buildBuddyAppState = 1
                buildBuddySharedPreference.apply {
                    buildBuddyExpired = buildBuddyData.buildBuddyExpires
                    buildBuddySavedUrl = buildBuddyData.buildBuddyUrl
                }
                _buildBuddyHomeScreenState.value =
                    BuildBuddyHomeScreenState.BuildBuddySuccess(buildBuddyData.buildBuddyUrl)
            }
        } else  {
            if (buildBuddyData == null) {
                _buildBuddyHomeScreenState.value =
                    BuildBuddyHomeScreenState.BuildBuddySuccess(buildBuddySharedPreference.buildBuddySavedUrl)
            } else {
                buildBuddySharedPreference.apply {
                    buildBuddyExpired = buildBuddyData.buildBuddyExpires
                    buildBuddySavedUrl = buildBuddyData.buildBuddyUrl
                }
                _buildBuddyHomeScreenState.value =
                    BuildBuddyHomeScreenState.BuildBuddySuccess(buildBuddyData.buildBuddyUrl)
            }
        }
    }


    sealed class BuildBuddyHomeScreenState {
        data object BuildBuddyLoading : BuildBuddyHomeScreenState()
        data object BuildBuddyError : BuildBuddyHomeScreenState()
        data class BuildBuddySuccess(val data: String) : BuildBuddyHomeScreenState()
        data object BuildBuddyNotInternet: BuildBuddyHomeScreenState()
    }
}