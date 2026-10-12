package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainScreen
import com.example.ui.Target100ViewModel
import com.example.ui.theme.Target100Theme

class MainActivity : ComponentActivity() {

    private val viewModel: Target100ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.appThemeMode.collectAsState()
            val accentTheme by viewModel.appAccentTheme.collectAsState()

            Target100Theme(
                themeMode = themeMode,
                accentTheme = accentTheme
            ) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
