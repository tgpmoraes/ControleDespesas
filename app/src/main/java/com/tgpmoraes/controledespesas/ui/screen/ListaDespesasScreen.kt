package com.tgpmoraes.controledespesas.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tgpmoraes.controledespesas.data.model.Despesa
import com.tgpmoraes.controledespesas.ui.DespesaItem
import com.tgpmoraes.controledespesas.ui.viewmodel.ListaDespesasViewModel
import java.util.Calendar

@Composable
fun ListaDespesasScreen(
    onEditar: (Despesa) -> Unit,
    onVoltar: () -> Unit,
    viewModel: ListaDespesasViewModel = viewModel()
) {
    val despesas = viewModel.despesas.value
    var despesaParaExcluir by remember { mutableStateOf<Despesa?>(null) }

    val hoje = remember { Calendar.getInstance() }
    var mesSelecionado by remember { mutableStateOf(hoje.get(Calendar.MONTH)) }
    var anoSelecionado by remember { mutableStateOf(hoje.get(Calendar.YEAR)) }

    // 🔄 carrega sempre que mês ou ano mudar
    LaunchedEffect(mesSelecionado, anoSelecionado) {
        viewModel.carregarDespesasDoMes(
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

        Button(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔽 Seletor mês/ano (mesma UX do dashboard)
        SeletorMesAno(
            mes = mesSelecionado,
            ano = anoSelecionado,
            onMesChange = { mesSelecionado = it },
            onAnoChange = { anoSelecionado = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(despesas) { despesa ->
                DespesaItem(
                    despesa = despesa,
                    onEditar = onEditar,
                    onExcluir = {
                        despesaParaExcluir = it
                    }
                )
            }
        }
    }

    // 🔴 Diálogo de confirmação
    despesaParaExcluir?.let { despesa ->
        AlertDialog(
            onDismissRequest = { despesaParaExcluir = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Deseja realmente excluir esta despesa?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.excluirDespesa(despesa)
                        despesaParaExcluir = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { despesaParaExcluir = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
