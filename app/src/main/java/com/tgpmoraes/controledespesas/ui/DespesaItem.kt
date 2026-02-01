package com.tgpmoraes.controledespesas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgpmoraes.controledespesas.data.model.Despesa

@Composable
fun DespesaItem(
    despesa: Despesa,
    onEditar: (Despesa) -> Unit,
    onExcluir: (Despesa) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(text = despesa.descricao)
        Text(text = "R$ ${despesa.valor}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { onEditar(despesa) }) {
                Text("Editar")
            }
            TextButton(onClick = { onExcluir(despesa) }) {
                Text("Excluir")
            }
        }
    }

    Divider()
}
