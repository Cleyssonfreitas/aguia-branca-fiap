package com.aguiabranca.inovacao.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Message(message: String?, onDismiss: () -> Unit) {
    if (message != null) {
        Spacer(Modifier.height(8.dp))
        ItemCard {
            Text(message)
            TextButton(onClick = onDismiss) { Text("Ok") }
        }
    }
}
