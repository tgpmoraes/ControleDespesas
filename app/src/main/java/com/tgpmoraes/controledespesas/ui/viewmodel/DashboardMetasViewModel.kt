package com.tgpmoraes.controledespesas.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tgpmoraes.controledespesas.data.model.MetaResumo
import com.tgpmoraes.controledespesas.data.repository.DespesaRepository
import com.tgpmoraes.controledespesas.domain.util.DateUtils
import androidx.compose.runtime.State
import com.tgpmoraes.controledespesas.data.model.Classificacao
import com.tgpmoraes.controledespesas.data.model.Fonte


class DashboardMetasViewModel : ViewModel() {

    private val repository = DespesaRepository()

    private val _metas = mutableStateOf<List<MetaResumo>>(emptyList())
    val metas: State<List<MetaResumo>> = _metas

    private var classificacoes: List<Classificacao> = emptyList()
    private var fontes: List<Fonte> = emptyList()

    init {
        repository.buscarClassificacoes {
            classificacoes = it
        }
        repository.buscarFontes {
            fontes = it
        }
    }

    fun carregarMetasDoMes(ano: Int, mes: Int) {

        val (inicioMes, fimMes) = DateUtils.periodoMes(ano, mes)

        val ciclosPorFonte = fontes
            .filter { it.diaInicioCiclo != null }
            .associate { fonte ->
                fonte.id to DateUtils.calcularPeriodoCiclo(
                    fonte.diaInicioCiclo!!,
                    ano,
                    mes
                )
            }

        // 🔍 intervalo mínimo necessário para busca única no Firestore
        val inicioBusca = listOf(
            inicioMes,
            ciclosPorFonte.values.minOfOrNull { it.first }
        ).filterNotNull().minOrNull()!!

        val fimBusca = listOf(
            fimMes,
            ciclosPorFonte.values.maxOfOrNull { it.second }
        ).filterNotNull().maxOrNull()!!

        repository.buscarDespesasDoPeriodo(inicioBusca, fimBusca) { despesas ->

            val despesasValidas = despesas.filter { despesa ->
                val fonte = fontes.find { it.id == despesa.fonteId }

                when (fonte?.diaInicioCiclo) {
                    null -> despesa.data >= inicioMes && despesa.data < fimMes
                    else -> {
                        val (inicio, fim) = ciclosPorFonte[fonte.id] ?: return@filter false
                        despesa.data >= inicio && despesa.data < fim
                    }
                }
            }

            val resultado = despesasValidas
                .groupBy { it.classificacaoId }
                .map { (classificacaoId, lista) ->

                    val classificacao =
                        classificacoes.find { it.id == classificacaoId }

                    MetaResumo(
                        classificacaoNome = classificacao?.nome ?: "Sem classificação",
                        metaMensal = classificacao?.metaMensal ?: 0.0,
                        totalGasto = lista.sumOf { it.valor }
                    )
                }

            _metas.value = resultado
        }
    }
}

