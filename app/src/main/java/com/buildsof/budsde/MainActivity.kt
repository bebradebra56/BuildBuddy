package com.buildsof.budsde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.buildsof.budsde.data.WorkType
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication
import com.buildsof.budsde.navigation.Screen
import com.buildsof.budsde.ui.screens.*
import com.buildsof.budsde.ui.theme.BuildBuddyTheme
import com.buildsof.budsde.viewmodel.*

class MainActivity : ComponentActivity() {
    
    private val container by lazy { (application as BuildBuddyApplication).container }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val factory = ViewModelFactory(container.projectRepository, container.settingsRepository)
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val settings by settingsViewModel.settings.collectAsState()
            
            BuildBuddyTheme(
                darkTheme = settings.isDarkTheme
            ) {
                BuildBuddyApp(factory)
            }
        }
    }
}

@Composable
fun BuildBuddyApp(factory: ViewModelFactory) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = viewModel(factory = factory)
            val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
            
            SplashScreen(
                onNavigateToNext = {
                    if (hasCompletedOnboarding) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = viewModel(factory = factory)
            
            OnboardingScreen(
                onComplete = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Dashboard Screen
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(factory = factory)
            val projects by viewModel.projects.collectAsState()
            
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToCreateProject = {
                    navController.navigate(Screen.CreateProject.route)
                },
                onNavigateToProjects = {
                    navController.navigate(Screen.MyProjects.route)
                },
                onNavigateToTemplates = {
                    navController.navigate(Screen.Templates.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToShoppingList = {
                    val firstProjectId = projects.firstOrNull()?.id
                    if (firstProjectId != null) {
                        navController.navigate(Screen.ShoppingList.createRoute(firstProjectId))
                    }
                },
                onNavigateToTips = {
                    navController.navigate(Screen.Tips.route)
                },
                onNavigateToCalculator = {
                    navController.navigate(Screen.Calculator.route)
                },
                onNavigateToProjectDetail = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }
        
        // Create Project Screen
        composable(Screen.CreateProject.route) {
            val viewModel: CreateProjectViewModel = viewModel(factory = factory)
            
            CreateProjectScreen(
                viewModel = viewModel,
                onProjectCreated = { projectId ->
                    navController.navigate(Screen.SelectZone.createRoute(projectId)) {
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Select Zone Screen
        composable(
            route = Screen.SelectZone.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val viewModel: SelectZoneViewModel = viewModel(factory = factory)
            
            SelectZoneScreen(
                projectId = projectId,
                viewModel = viewModel,
                onZoneSelected = { roomId ->
                    navController.navigate(Screen.EnterDimensions.createRoute(projectId, roomId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Enter Dimensions Screen
        composable(
            route = Screen.EnterDimensions.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val viewModel: EnterDimensionsViewModel = viewModel(factory = factory)
            
            EnterDimensionsScreen(
                projectId = projectId,
                roomId = roomId,
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(Screen.SelectWorks.createRoute(projectId, roomId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Select Works Screen
        composable(
            route = Screen.SelectWorks.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val viewModel: SelectWorksViewModel = viewModel(factory = factory)
            
            SelectWorksScreen(
                projectId = projectId,
                roomId = roomId,
                viewModel = viewModel,
                onWorkConfigureClick = { workItemId, workType ->
                    when (workType) {
                        WorkType.PAINT_WALLS, WorkType.CEILING_PAINT -> {
                            navController.navigate(Screen.ConfigurePaint.createRoute(projectId, roomId, workItemId))
                        }
                        WorkType.WALLPAPER -> {
                            navController.navigate(Screen.ConfigureWallpaper.createRoute(projectId, roomId, workItemId))
                        }
                        WorkType.WALL_TILES, WorkType.FLOOR_TILES -> {
                            navController.navigate(Screen.ConfigureTile.createRoute(projectId, roomId, workItemId))
                        }
                        WorkType.LAMINATE -> {
                            navController.navigate(Screen.ConfigureLaminate.createRoute(projectId, roomId, workItemId))
                        }
                        WorkType.BASEBOARD, WorkType.PRIMER, WorkType.PUTTY -> {
                            navController.navigate("configure_simple/$projectId/$roomId/$workItemId/${workType.name}")
                        }
                    }
                },
                onContinue = {
                    navController.navigate(Screen.Preview.createRoute(projectId, roomId))
                },
                onNavigateBack = { navController.navigateUp() },
                onEditDimensions = {
                    navController.navigate(Screen.EnterDimensions.createRoute(projectId, roomId))
                }
            )
        }
        
        // Configure Paint Screen
        composable(
            route = Screen.ConfigurePaint.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType },
                navArgument("workItemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val workItemId = backStackEntry.arguments?.getString("workItemId") ?: return@composable
            val viewModel: ConfigurePaintViewModel = viewModel(factory = factory)
            
            ConfigurePaintScreen(
                projectId = projectId,
                roomId = roomId,
                workItemId = workItemId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Configure Wallpaper Screen
        composable(
            route = Screen.ConfigureWallpaper.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType },
                navArgument("workItemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val workItemId = backStackEntry.arguments?.getString("workItemId") ?: return@composable
            val viewModel: ConfigureWallpaperViewModel = viewModel(factory = factory)
            
            ConfigureWallpaperScreen(
                projectId = projectId,
                roomId = roomId,
                workItemId = workItemId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Configure Tile Screen
        composable(
            route = Screen.ConfigureTile.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType },
                navArgument("workItemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val workItemId = backStackEntry.arguments?.getString("workItemId") ?: return@composable
            val viewModel: ConfigureTileViewModel = viewModel(factory = factory)
            
            ConfigureTileScreen(
                projectId = projectId,
                roomId = roomId,
                workItemId = workItemId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Configure Laminate Screen
        composable(
            route = Screen.ConfigureLaminate.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType },
                navArgument("workItemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val workItemId = backStackEntry.arguments?.getString("workItemId") ?: return@composable
            val viewModel: ConfigureLaminateViewModel = viewModel(factory = factory)
            
            ConfigureLaminateScreen(
                projectId = projectId,
                roomId = roomId,
                workItemId = workItemId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Configure Simple Work Screen (Baseboard, Primer, Putty)
        composable(
            route = "configure_simple/{projectId}/{roomId}/{workItemId}/{workType}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType },
                navArgument("workItemId") { type = NavType.StringType },
                navArgument("workType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val workItemId = backStackEntry.arguments?.getString("workItemId") ?: return@composable
            val workTypeName = backStackEntry.arguments?.getString("workType") ?: return@composable
            val workType = try {
                com.buildsof.budsde.data.WorkType.valueOf(workTypeName)
            } catch (e: Exception) {
                return@composable
            }
            val viewModel: ConfigureSimpleWorkViewModel = viewModel(factory = factory)
            
            ConfigureSimpleWorkScreen(
                projectId = projectId,
                roomId = roomId,
                workItemId = workItemId,
                workType = workType,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Preview Screen
        composable(
            route = Screen.Preview.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val viewModel: PreviewViewModel = viewModel(factory = factory)
            
            PreviewScreen(
                projectId = projectId,
                roomId = roomId,
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(Screen.Budget.createRoute(projectId))
                },
                onEdit = { navController.navigateUp() },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Budget Screen
        composable(
            route = Screen.Budget.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val viewModel: BudgetViewModel = viewModel(factory = factory)
            
            BudgetScreen(
                projectId = projectId,
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(Screen.ShoppingList.createRoute(projectId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Shopping List Screen
        composable(
            route = Screen.ShoppingList.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val viewModel: ShoppingListViewModel = viewModel(factory = factory)
            
            ShoppingListScreen(
                projectId = projectId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Shop Mode Screen
        composable(
            route = Screen.ShopMode.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val viewModel: ShopModeViewModel = viewModel(factory = factory)
            
            ShopModeScreen(
                projectId = projectId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Project Detail Screen
        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val viewModel: ProjectDetailViewModel = viewModel(factory = factory)
            
            ProjectDetailScreen(
                projectId = projectId,
                viewModel = viewModel,
                onNavigateToNotes = {
                    navController.navigate(Screen.Notes.createRoute(projectId))
                },
                onNavigateToShoppingList = {
                    navController.navigate(Screen.ShoppingList.createRoute(projectId))
                },
                onNavigateToRoom = { roomId ->
                    navController.navigate(Screen.SelectWorks.createRoute(projectId, roomId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Notes Screen
        composable(
            route = Screen.Notes.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val viewModel: NotesViewModel = viewModel(factory = factory)
            
            NotesScreen(
                projectId = projectId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // My Projects Screen
        composable(Screen.MyProjects.route) {
            val viewModel: MyProjectsViewModel = viewModel(factory = factory)
            
            MyProjectsScreen(
                viewModel = viewModel,
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Templates Screen
        composable(Screen.Templates.route) {
            val viewModel: TemplatesViewModel = viewModel(factory = factory)
            
            TemplatesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Settings Screen
        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(factory = factory)
            
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Tips Screen
        composable(Screen.Tips.route) {
            TipsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Calculator Screen
        composable(Screen.Calculator.route) {
            CalculatorScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}