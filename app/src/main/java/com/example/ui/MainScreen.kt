package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.AppTopBar
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AiTestScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RevisionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.TargetsScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: Target100ViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val currentLang by viewModel.appLanguage.collectAsState()
    val streakDays by viewModel.currentStreak.collectAsState()
    val examDaysRemaining by viewModel.examDaysRemaining.collectAsState()
    val verifiedCount by viewModel.verifiedChaptersCount.collectAsState()
    val totalChapters by viewModel.totalVisibleChaptersCount.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button: close drawer first, or return to Dashboard
    BackHandler(enabled = drawerState.isOpen || currentDestination != AppNavDestination.DASHBOARD) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (currentDestination != AppNavDestination.DASHBOARD) {
            viewModel.navigateTo(AppNavDestination.DASHBOARD)
        }
    }

    val totalXp by viewModel.totalXp.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentDestination = currentDestination,
                currentLang = currentLang,
                verifiedCount = verifiedCount,
                totalChapters = totalChapters,
                streakDays = streakDays,
                totalXp = totalXp,
                examDaysRemaining = examDaysRemaining,
                onDestinationClick = { dest ->
                    viewModel.navigateTo(dest)
                    scope.launch { drawerState.close() }
                },
                onLangToggle = {
                    val next = if (currentLang == "hi") "en" else "hi"
                    viewModel.setLanguage(next)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    currentDestination = currentDestination,
                    streakDays = streakDays,
                    examDaysRemaining = examDaysRemaining,
                    currentLang = currentLang,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onLangToggle = {
                        val next = if (currentLang == "hi") "en" else "hi"
                        viewModel.setLanguage(next)
                    }
                )
            }
        ) { paddingValues ->
            val screenModifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

            when (currentDestination) {
                AppNavDestination.DASHBOARD -> DashboardScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.TARGETS -> TargetsScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.SYLLABUS -> SyllabusScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.REVISION -> RevisionScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.AI_TESTS -> AiTestScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.REPORTS -> ReportsScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel, modifier = screenModifier)
                AppNavDestination.SETTINGS -> SettingsScreen(viewModel = viewModel, modifier = screenModifier)
            }
        }
    }
}
