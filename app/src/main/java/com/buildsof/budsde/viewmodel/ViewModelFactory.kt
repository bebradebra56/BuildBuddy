package com.buildsof.budsde.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository

class ViewModelFactory(
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(settingsRepository) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(settingsRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(CreateProjectViewModel::class.java) -> {
                CreateProjectViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(SelectZoneViewModel::class.java) -> {
                SelectZoneViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(EnterDimensionsViewModel::class.java) -> {
                EnterDimensionsViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(SelectWorksViewModel::class.java) -> {
                SelectWorksViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(ConfigurePaintViewModel::class.java) -> {
                ConfigurePaintViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(ConfigureWallpaperViewModel::class.java) -> {
                ConfigureWallpaperViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(ConfigureTileViewModel::class.java) -> {
                ConfigureTileViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(ConfigureLaminateViewModel::class.java) -> {
                ConfigureLaminateViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(ConfigureSimpleWorkViewModel::class.java) -> {
                ConfigureSimpleWorkViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(PreviewViewModel::class.java) -> {
                PreviewViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> {
                BudgetViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(ShoppingListViewModel::class.java) -> {
                ShoppingListViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(ProjectDetailViewModel::class.java) -> {
                ProjectDetailViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(MyProjectsViewModel::class.java) -> {
                MyProjectsViewModel(projectRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository) as T
            }
            modelClass.isAssignableFrom(TemplatesViewModel::class.java) -> {
                TemplatesViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(NotesViewModel::class.java) -> {
                NotesViewModel(projectRepository) as T
            }
            modelClass.isAssignableFrom(ShopModeViewModel::class.java) -> {
                ShopModeViewModel(projectRepository, settingsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
