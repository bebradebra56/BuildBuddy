package com.buildsof.budsde.gort.domain.usecases

import android.util.Log
import com.buildsof.budsde.gort.data.repo.BuildBuddyRepository
import com.buildsof.budsde.gort.data.utils.BuildBuddyPushToken
import com.buildsof.budsde.gort.data.utils.BuildBuddySystemService
import com.buildsof.budsde.gort.domain.model.BuildBuddyEntity
import com.buildsof.budsde.gort.domain.model.BuildBuddyParam
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication

class BuildBuddyGetAllUseCase(
    private val buildBuddyRepository: BuildBuddyRepository,
    private val buildBuddySystemService: BuildBuddySystemService,
    private val buildBuddyPushToken: BuildBuddyPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BuildBuddyEntity?{
        val params = BuildBuddyParam(
            buildBuddyLocale = buildBuddySystemService.buildBuddyGetLocale(),
            buildBuddyPushToken = buildBuddyPushToken.buildBuddyGetToken(),
            buildBuddyAfId = buildBuddySystemService.buildBuddyGetAppsflyerId()
        )
        Log.d(BuildBuddyApplication.BUILD_BUDDY_MAIN_TAG, "Params for request: $params")
        return buildBuddyRepository.buildBuddyGetClient(params, conversion)
    }



}