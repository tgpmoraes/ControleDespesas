package com.tgpmoraes.controledespesas.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tgpmoraes.controledespesas.data.model.Classificacao
import com.tgpmoraes.controledespesas.data.model.Despesa
import com.tgpmoraes.controledespesas.data.model.Fonte
import com.tgpmoraes.controledespesas.ui.viewmodel.DespesaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroDespesaScreen(
    despesaParaEdicao: Despesa? = null,
    onVoltar: () -> Unit,
    viewModel: DespesaViewModel = viewModel()
) {

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var parcelado by remember { mutableStateOf(false) }
    var numeroParcelas by remember { mutableStateOf("1") }

    LaunchedEffect(despesaParaEdicao) {
        despesaParaEdicao?.let {
            descricao = it.descricao
            valor = it.valor.toString()
            parcelado = it.totalParcelas > 1
            numeroParcelas = it.totalParcelas.toString()
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var classificacaoSelecionada by remember { mutableStateOf<Classificacao?>(null) }

    var fonteSelecionada by remember { mutableStateOf<Fonte?>(null) }
    var expandedFonte by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.classificacoes.value) {
        classificacaoSelecionada =
            viewModel.classificacoes.value
                .find { it.id == despesaParaEdicao?.classificacaoId }
    }

    LaunchedEffect(viewModel.fontes.value) {
        fonteSelecionada =
            viewModel.fontes.value
                .find { it.id == despesaParaEdicao?.fonteId }
    }

    var recorrente by remember {
        mutableStateOf(despesaParaEdicao?.recorrenteId != null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        /* BOTÃO VOLTAR */
        Button(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* FORMULÁRIO */

        TextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = recorrente,
                onCheckedChange = { recorrente = it }
            )
            Text("Despesa recorrente")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (recorrente) {
            parcelado = false
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = parcelado,
                onCheckedChange = { parcelado = it }
            )
            Text("Despesa parcelada")
        }

        if (parcelado) {
            TextField(
                value = numeroParcelas,
                onValueChange = { numeroParcelas = it },
                label = { Text("Número de parcelas") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = classificacaoSelecionada?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Classificação") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                viewModel.classificacoes.value.forEach { classificacao ->
                    DropdownMenuItem(
                        text = { Text(classificacao.nome) },
                        onClick = {
                            classificacaoSelecionada = classificacao
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedFonte,
            onExpandedChange = { expandedFonte = !expandedFonte }
        ) {
            TextField(
                value = fonteSelecionada?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fonte") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedFonte,
                onDismissRequest = { expandedFonte = false }
            ) {
                viewModel.fontes.value.forEach { fonte ->
                    DropdownMenuItem(
                        text = { Text(fonte.nome) },
                        onClick = {
                            fonteSelecionada = fonte
                            expandedFonte = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    despesaParaEdicao != null -> {
                        viewModel.atualizarDespesa(
                            id = despesaParaEdicao.id,
                            descricao = descricao,
                            valor = valor.toDoubleOrNull() ?: 0.0,
                            classificacao = classificacaoSelecionada,
                            fonte = fonteSelecionada
                        )
                    }

                    recorrente -> {
                        viewModel.salvarDespesaRecorrente(
                            descricao = descricao,
                            valor = valor.toDoubleOrNull() ?: 0.0,
                            classificacao = classificacaoSelecionada,
                            fonte = fonteSelecionada
                        )
                    }

                    else -> {
                        viewModel.salvarDespesa(
                            descricao = descricao,
                            valor = valor.toDoubleOrNull() ?: 0.0,
                            parcelas = if (parcelado)
                                numeroParcelas.toIntOrNull() ?: 1
                            else 1,
                            classificacao = classificacaoSelecionada,
                            fonte = fonteSelecionada
                        )
                    }
                }
                onVoltar()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (despesaParaEdicao == null)
                    "Salvar despesa"
                else
                    "Atualizar despesa"
            )
        }
    }
}
