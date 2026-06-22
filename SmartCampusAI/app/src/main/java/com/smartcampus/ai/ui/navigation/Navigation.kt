package com.smartcampus.ai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.smartcampus.ai.ui.screens.ai_assistant.AiAssistantScreen
import com.smartcampus.ai.ui.screens.attendance.AttendanceScreen
import com.smartcampus.ai.ui.screens.auth.LoginScreen
import com.smartcampus.ai.ui.screens.auth.SignupScreen
import com.smartcampus.ai.ui.screens.home.HomeScreen
import com.smartcampus.ai.ui.screens.notes.AddEditNoteScreen
import com.smartcampus.ai.ui.screens.notes.NotesScreen
import com.smartcampus.ai.ui.screens.pomodoro.PomodoroScreen
import com.smartcampus.ai.ui.screens.settings.SettingsScreen
import com.smartcampus.ai.ui.screens.tasks.AddEditTaskScreen
import com.smartcampus.ai.ui.screens.tasks.TasksScreen
import com.smartcampus.ai.ui.screens.timetable.TimetableScreen

// ─────────────────────────────────────────────
//  ROUTE DEFINITIONS
// ─────────────────────────────────────────────
sealed class Screen(val route: String) {
    // Auth
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")

    // Main
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object AddEditTask : Screen("add_edit_task?taskId={taskId}") {
        fun createRoute(taskId: Int = -1) = "add_edit_task?taskId=$taskId"
    }
    object Notes : Screen("notes")
    object AddEditNote : Screen("add_edit_note?noteId={noteId}") {
        fun createRoute(noteId: Int = -1) = "add_edit_note?noteId=$noteId"
    }
    object Attendance : Screen("attendance")
    object Timetable : Screen("timetable")
    object AiAssistant : Screen("ai_assistant")
    object Pomodoro : Screen("pomodoro")
    object Settings : Screen("settings")
}

// ─────────────────────────────────────────────
//  BOTTOM NAV ITEMS
// ─────────────────────────────────────────────
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Default.Home),
    BottomNavItem("Tasks", Screen.Tasks.route, Icons.Default.Assignment),
    BottomNavItem("Notes", Screen.Notes.route, Icons.Default.Note),
    BottomNavItem("AI", Screen.AiAssistant.route, Icons.Default.AutoAwesome),
    BottomNavItem("Settings", Screen.Settings.route, Icons.Default.Settings)
)

// ─────────────────────────────────────────────
//  MAIN NAV GRAPH
// ─────────────────────────────────────────────
@Composable
fun SmartCampusNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300), initialOffsetX = { it / 4 }
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(300), targetOffsetX = { -it / 4 }
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300), initialOffsetX = { -it / 4 }
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(300), targetOffsetX = { it / 4 }
            )
        }
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(Screen.Signup.route) }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        // Main Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateTo = { navController.navigate(it) },
                onPomodoroClick = { navController.navigate(Screen.Pomodoro.route) },
                onAttendanceClick = { navController.navigate(Screen.Attendance.route) },
                onTimetableClick = { navController.navigate(Screen.Timetable.route) }
            )
        }
        composable(Screen.Tasks.route) {
            TasksScreen(
                onAddTask = { navController.navigate(Screen.AddEditTask.createRoute()) },
                onEditTask = { id -> navController.navigate(Screen.AddEditTask.createRoute(id)) }
            )
        }
        composable(
            route = Screen.AddEditTask.route,
            arguments = listOf(navArgument("taskId") { defaultValue = -1 })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            AddEditTaskScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Notes.route) {
            NotesScreen(
                onAddNote = { navController.navigate(Screen.AddEditNote.createRoute()) },
                onEditNote = { id -> navController.navigate(Screen.AddEditNote.createRoute(id)) }
            )
        }
        composable(
            route = Screen.AddEditNote.route,
            arguments = listOf(navArgument("noteId") { defaultValue = -1 })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            AddEditNoteScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Attendance.route) {
            AttendanceScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Timetable.route) {
            TimetableScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.AiAssistant.route) {
            AiAssistantScreen()
        }
        composable(Screen.Pomodoro.route) {
            PomodoroScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
