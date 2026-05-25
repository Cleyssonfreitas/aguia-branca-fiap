package com.aguiabranca.inovacao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.navigation.AppContent
import com.aguiabranca.inovacao.presentation.theme.AguiaBrancaInovacaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = AppContainer(applicationContext)

        setContent {
            val themePreference by container.sessionManager.themeFlow.collectAsState(initial = 0)
            val isDarkTheme = when (themePreference) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            AguiaBrancaInovacaoTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(container)
                }
            }
        }
    }
}
