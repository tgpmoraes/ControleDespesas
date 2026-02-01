package com.tgpmoraes.controledespesas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgpmoraes.controledespesas.data.model.MetaResumo

@Composable
fun MetaCard(meta: MetaResumo) {
    val percentual = meta.percentual.toFloat()

    val progressoNormalizado = percentual.coerceAtMost(1f)

    val corProgresso = if (percentual > 1f) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = meta.classificacaoNome,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progressoNormalizado,
                color = corProgresso,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "R$ %.2f / R$ %.2f (%.0f%%)"
                    .format(
                        meta.totalGasto,
                        meta.metaMensal,
                        percentual * 100
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = if (percentual > 1f)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

