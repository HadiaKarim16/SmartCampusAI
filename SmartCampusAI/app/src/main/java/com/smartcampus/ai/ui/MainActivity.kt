package com.smartcampus.ai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.smartcampus.ai.data.preferences.PreferencesManager
import com.smartcampus.ai.ui.navigation.*
import com.smartcampus.ai.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by preferencesManager.isDarkMode.collectAsStateWithLifecycle(initialValue = true)
            val isLoggedIn by preferencesManager.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

            SmartCampusTheme(darkTheme = isDarkMode) {
                SmartCampusApp(isLoggedIn = isLoggedIn)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  MAIN APP SCAFFOLD
// ─────────────────────────────────────────────
@Composable
fun SmartCampusApp(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    // Screens that show the bottom navigation bar
    val bottomNavRoutes = setOf(
        Screen.Home.route,
        Screen.Tasks.route,
        Screen.Notes.route,
        Screen.AiAssistant.route,
        Screen.Settings.route
    )

    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        containerColor = SmartCampusColors.Background,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                SmartCampusBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        SmartCampusNavGraph(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            )
        )
    }
}

// ─────────────────────────────────────────────
//  BOTTOM NAVIGATION BAR
// ─────────────────────────────────────────────
@Composable
fun SmartCampusBottomBar(
    navController: androidx.navigation.NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar(
        containerColor = SmartCampusColors.Surface,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(if (isSelected) 24.dp else 22.dp)
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SmartCampusColors.Primary,
                    selectedTextColor = SmartCampusColors.Primary,
                    unselectedIconColor = SmartCampusColors.OnSurfaceVariant,
                    unselectedTextColor = SmartCampusColors.OnSurfaceVariant,
                    indicatorColor = SmartCampusColors.Primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
