package com.buildsof.budsde.gort.presentation.di

import com.buildsof.budsde.gort.data.repo.BuildBuddyRepository
import com.buildsof.budsde.gort.data.shar.BuildBuddySharedPreference
import com.buildsof.budsde.gort.data.utils.BuildBuddyPushToken
import com.buildsof.budsde.gort.data.utils.BuildBuddySystemService
import com.buildsof.budsde.gort.domain.usecases.BuildBuddyGetAllUseCase
import com.buildsof.budsde.gort.presentation.pushhandler.BuildBuddyPushHandler
import com.buildsof.budsde.gort.presentation.ui.load.BuildBuddyLoadViewModel
import com.buildsof.budsde.gort.presentation.ui.view.BuildBuddyViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val buildBuddyModule = module {
    factory {
        BuildBuddyPushHandler()
    }
    single {
        BuildBuddyRepository()
    }
    single {
        BuildBuddySharedPreference(get())
    }
    factory {
        BuildBuddyPushToken()
    }
    factory {
        BuildBuddySystemService(get())
    }
    factory {
        BuildBuddyGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BuildBuddyViFun(get())
    }
    viewModel {
        BuildBuddyLoadViewModel(get(), get(), get())
    }
}