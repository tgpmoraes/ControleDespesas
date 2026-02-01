package com.tgpmoraes.controledespesas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tgpmoraes.controledespesas.ui.components.MetaCard
import com.tgpmoraes.controledespesas.ui.viewmodel.DashboardMetasViewModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardMetasScreen(
    onNovaDespesa: () -> Unit,
    onVerLista: (ano: Int, mes: Int) -> Unit,
    viewModel: DashboardMetasViewModel = viewModel()
) {

    val metas by viewModel.metas

    val hoje = remember { Calendar.getInstance() }
    var mesSelecionado by remember { mutableStateOf(hoje.get(Calendar.MONTH)) }
    var anoSelecionado by remember { mutableStateOf(hoje.get(Calendar.YEAR)) }

    LaunchedEffect(mesSelecionado, anoSelecionado) {
        viewModel.carregarMetasDoMes(
            ano = anoSelecionado,
            mes = mesSelecionado + 1
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {

        SeletorMesAno(
            mes = mesSelecionado,
            ano = anoSelecionado,
            onMesChange = { mesSelecionado = it },
            onAnoChange = { anoSelecionado = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Resumo Mensal",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(metas) { meta ->
                MetaCard(meta)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNovaDespesa,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nova despesa")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onVerLista(anoSelecionado, mesSelecionado + 1)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver despesas do mês")
        }
    }
}
