package com.tgpmoraes.controledespesas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SeletorMesAno(
    mes: Int,
    ano: Int,
    onMesChange: (Int) -> Unit,
    onAnoChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (mes == 0) {
                onMesChange(11)
                onAnoChange(ano - 1)
            } else {
                onMesChange(mes - 1)
            }
        }) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Mês anterior"
            )
        }

        Text(
            text = "${
                Month.of(mes + 1)
                    .getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                    .replaceFirstChar { it.uppercase() }
            } / $ano",
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = {
            if (mes == 11) {
                onMesChange(0)
                onAnoChange(ano + 1)
            } else {
                onMesChange(mes + 1)
            }
        }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Próximo mês"
            )
        }
    }
}
