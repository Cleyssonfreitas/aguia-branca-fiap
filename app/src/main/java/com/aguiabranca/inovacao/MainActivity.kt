package com.aguiabranca.inovacao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.navigation.AppContent
import com.aguiabranca.inovacao.presentation.theme.AguiaBrancaInovacaoTheme
import com.aguiabranca.inovacao.presentation.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = AppContainer(applicationContext)
        val viewModel = ViewModelProvider(this, AppViewModel.Factory(container))[AppViewModel::class.java]

        setContent {
            AguiaBrancaInovacaoTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(viewModel)
                }
            }
        }
    }
}
