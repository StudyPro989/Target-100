package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNavDestination
import com.example.ui.i18n.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentDestination: AppNavDestination,
    streakDays: Int,
    examDaysRemaining: Int,
    currentLang: String,
    onMenuClick: () -> Unit,
    onLangToggle: () -> Unit
) {
    val title = when (currentDestination) {
        AppNavDestination.DASHBOARD -> AppStrings.get("nav_dashboard", currentLang)
        AppNavDestination.TARGETS -> AppStrings.get("nav_targets", currentLang)
        AppNavDestination.SYLLABUS -> AppStrings.get("nav_syllabus", currentLang)
        AppNavDestination.REVISION -> AppStrings.get("nav_revision", currentLang)
        AppNavDestination.AI_TESTS -> AppStrings.get("nav_ai_tests", currentLang)
        AppNavDestination.REPORTS -> AppStrings.get("nav_reports", currentLang)
        AppNavDestination.ACHIEVEMENTS -> AppStrings.get("nav_achievements", currentLang)
        AppNavDestination.SETTINGS -> AppStrings.get("nav_settings", currentLang)
    }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (examDaysRemaining > 0) {
                    Text(
                        text = "RBSE Board: $examDaysRemaining ${AppStrings.get("dash_days_remaining", currentLang)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open navigation menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            // Streak Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streakDays",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Language Toggle Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onLangToggle() }
            ) {
                Text(
                    text = if (currentLang == "hi") "EN" else "हिं",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun AppDrawerContent(
    currentDestination: AppNavDestination,
    currentLang: String,
    verifiedCount: Int,
    totalChapters: Int,
    onDestinationClick: (AppNavDestination) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.85f),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // App Branding Header
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "100",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = AppStrings.get("app_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "RBSE Class 10 • Rajasthan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$verifiedCount/$totalChapters ${AppStrings.get("dash_verified_chapters", currentLang)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

            // Navigation Items
            val navItems = listOf(
                Triple(AppNavDestination.DASHBOARD, AppStrings.get("nav_dashboard", currentLang), Icons.Default.Dashboard),
                Triple(AppNavDestination.TARGETS, AppStrings.get("nav_targets", currentLang), Icons.Default.Assignment),
                Triple(AppNavDestination.SYLLABUS, AppStrings.get("nav_syllabus", currentLang), Icons.Default.MenuBook),
                Triple(AppNavDestination.REVISION, AppStrings.get("nav_revision", currentLang), Icons.Default.Refresh),
                Triple(AppNavDestination.AI_TESTS, AppStrings.get("nav_ai_tests", currentLang), Icons.Default.AutoAwesome),
                Triple(AppNavDestination.REPORTS, AppStrings.get("nav_reports", currentLang), Icons.Default.Assessment),
                Triple(AppNavDestination.ACHIEVEMENTS, AppStrings.get("nav_achievements", currentLang), Icons.Default.EmojiEvents),
                Triple(AppNavDestination.SETTINGS, AppStrings.get("nav_settings", currentLang), Icons.Default.Settings)
            )

            navItems.forEach { (destination, label, icon) ->
                val selected = currentDestination == destination
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = selected,
                    onClick = { onDestinationClick(destination) },
                    modifier = Modifier.padding(vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
