package com.tgpmoraes.controledespesas.ui

import androidx.compose.runtime.*
import com.tgpmoraes.controledespesas.data.model.Despesa
import com.tgpmoraes.controledespesas.ui.screen.CadastroDespesaScreen
import com.tgpmoraes.controledespesas.ui.screen.DashboardMetasScreen
import com.tgpmoraes.controledespesas.ui.screen.ListaDespesasScreen
import java.util.Calendar

@Composable
fun AppContent() {
    var telaAtual by remember { mutableStateOf("dashboard") }
    var despesaSelecionada by remember { mutableStateOf<Despesa?>(null) }

    // 🗓️ estado global do período selecionado
    var mesSelecionado by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var anoSelecionado by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    when (telaAtual) {
        "dashboard" -> DashboardMetasScreen(
            onNovaDespesa = { telaAtual = "cadastro" },
            onVerLista = { ano, mes ->
                anoSelecionado = ano
                mesSelecionado = mes
                telaAtual = "lista"
            }
        )

        "cadastro" -> CadastroDespesaScreen(
            despesaParaEdicao = despesaSelecionada,
            onVoltar = {
                despesaSelecionada = null
                telaAtual = "dashboard"
            }
        )
        "lista" -> ListaDespesasScreen(
            onEditar = { despesa ->
                despesaSelecionada = despesa
                telaAtual = "cadastro"
            },
            onVoltar = { telaAtual = "dashboard" }

        )
    }
}