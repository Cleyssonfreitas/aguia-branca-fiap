package com.aguiabranca.inovacao.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulBotao,
    secondary = LogoAzulClaro,
    tertiary = LogoAzulEscuro,
    background = Color(0xFF12131C),
    surface = Color(0xFF1E1F2B),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AzulBotao,           
    secondary = LogoAzulClaro,       
    tertiary = LogoAzulEscuro,       
    background = FundoBrancoAzulado, 
    surface = FundoBrancoAzulado,    
    
    onPrimary = Color.White,        
    onBackground = LetrasAzulEscuro, 
    onSurface = LetrasAzulEscuro
)

@Composable
fun AguiaBrancaInovacaoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}