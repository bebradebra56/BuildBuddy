package com.buildsof.budsde.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object CreateProject : Screen("create_project")
    object SelectZone : Screen("select_zone/{projectId}") {
        fun createRoute(projectId: String) = "select_zone/$projectId"
    }
    object EnterDimensions : Screen("enter_dimensions/{projectId}/{roomId}") {
        fun createRoute(projectId: String, roomId: String) = "enter_dimensions/$projectId/$roomId"
    }
    object SelectWorks : Screen("select_works/{projectId}/{roomId}") {
        fun createRoute(projectId: String, roomId: String) = "select_works/$projectId/$roomId"
    }
    object ConfigurePaint : Screen("configure_paint/{projectId}/{roomId}/{workItemId}") {
        fun createRoute(projectId: String, roomId: String, workItemId: String) = 
            "configure_paint/$projectId/$roomId/$workItemId"
    }
    object ConfigureWallpaper : Screen("configure_wallpaper/{projectId}/{roomId}/{workItemId}") {
        fun createRoute(projectId: String, roomId: String, workItemId: String) = 
            "configure_wallpaper/$projectId/$roomId/$workItemId"
    }
    object ConfigureTile : Screen("configure_tile/{projectId}/{roomId}/{workItemId}") {
        fun createRoute(projectId: String, roomId: String, workItemId: String) = 
            "configure_tile/$projectId/$roomId/$workItemId"
    }
    object ConfigureLaminate : Screen("configure_laminate/{projectId}/{roomId}/{workItemId}") {
        fun createRoute(projectId: String, roomId: String, workItemId: String) = 
            "configure_laminate/$projectId/$roomId/$workItemId"
    }
    object Preview : Screen("preview/{projectId}/{roomId}") {
        fun createRoute(projectId: String, roomId: String) = "preview/$projectId/$roomId"
    }
    object Budget : Screen("budget/{projectId}") {
        fun createRoute(projectId: String) = "budget/$projectId"
    }
    object ShoppingList : Screen("shopping_list/{projectId}") {
        fun createRoute(projectId: String) = "shopping_list/$projectId"
    }
    object ShopMode : Screen("shop_mode/{projectId}") {
        fun createRoute(projectId: String) = "shop_mode/$projectId"
    }
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    object Notes : Screen("notes/{projectId}") {
        fun createRoute(projectId: String) = "notes/$projectId"
    }
    object Templates : Screen("templates")
    object Settings : Screen("settings")
    object MyProjects : Screen("my_projects")
    object Purchases : Screen("purchases")
    object Tips : Screen("tips")
    object Calculator : Screen("calculator")
}
