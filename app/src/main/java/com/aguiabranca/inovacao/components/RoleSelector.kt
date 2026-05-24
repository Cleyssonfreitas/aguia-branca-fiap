package com.aguiabranca.inovacao.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aguiabranca.inovacao.models.UserRole
import com.aguiabranca.inovacao.utils.shortName

@Composable
fun RoleSelector(selected: UserRole, onSelect: (UserRole) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        UserRole.entries.forEach { role ->
            if (role == selected) {
                Button(onClick = { onSelect(role) }, modifier = Modifier.weight(1f)) { Text(role.shortName()) }
            } else {
                OutlinedButton(onClick = { onSelect(role) }, modifier = Modifier.weight(1f)) { Text(role.shortName()) }
            }
        }
    }
}
