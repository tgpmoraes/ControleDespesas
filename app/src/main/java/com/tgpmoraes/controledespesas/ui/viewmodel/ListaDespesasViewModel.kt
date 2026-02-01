package com.tgpmoraes.controledespesas.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tgpmoraes.controledespesas.data.model.Despesa
import com.tgpmoraes.controledespesas.data.repository.DespesaRepository
import com.tgpmoraes.controledespesas.domain.util.DateUtils.calcularPeriodoCiclo
import androidx.compose.runtime.State
import com.tgpmoraes.controledespesas.data.model.Fonte
import com.tgpmoraes.controledespesas.domain.util.DateUtils

class ListaDespesasViewModel : ViewModel() {

    private val repository = DespesaRepository()

    private val _despesas = mutableStateOf<List<Despesa>>(emptyList())
    val despesas: State<List<Despesa>> = _despesas

    private var inicioAtual: Long = 0L
    private var fimAtual: Long = 0L

    // dependências já existentes no app
    private var fontes: List<Fonte> = emptyList()

    init {
        repository.buscarFontes {
            fontes = it
        }
    }

    fun carregarDespesasDoMes(ano: Int, mes: Int) {

        // 📅 período do mês (débito)
        val (inicioMes, fimMes) = DateUtils.periodoMes(ano, mes)

        // 🔁 ciclos por fonte (crédito)
        val ciclosPorFonte = fontes
            .filter { it.diaInicioCiclo != null }
            .associate { fonte ->
                fonte.id to DateUtils.calcularPeriodoCiclo(
                    fonte.diaInicioCiclo!!,
                    ano,
                    mes
                )
            }

        // 🔍 intervalo mínimo para uma única query
        inicioAtual = listOf(
            inicioMes,
            ciclosPorFonte.values.minOfOrNull { it.first }
        ).filterNotNull().minOrNull()!!

        fimAtual = listOf(
            fimMes,
            ciclosPorFonte.values.maxOfOrNull { it.second }
        ).filterNotNull().maxOrNull()!!

        repository.buscarDespesasDoPeriodo(inicioAtual, fimAtual) { despesas ->

            val despesasValidas = despesas.filter { despesa ->
                val fonte = fontes.find { it.id == despesa.fonteId }

                when (fonte?.diaInicioCiclo) {
                    null -> despesa.data >= inicioMes && despesa.data < fimMes
                    else -> {
                        val (inicio, fim) =
                            ciclosPorFonte[fonte.id] ?: return@filter false
                        despesa.data >= inicio && despesa.data < fim
                    }
                }
            }

            _despesas.value = despesasValidas
        }
    }

    fun excluirDespesa(despesa: Despesa) {

        // 🔁 exclui recorrência se existir
        despesa.recorrenteId
            ?.takeIf { it.isNotBlank() }
            ?.let { repository.excluirDespesaRecorrente(it) }

        repository.excluirDespesa(despesa.id)

        // 🔄 recarrega lista com mesmo período
        repository.buscarDespesasDoPeriodo(inicioAtual, fimAtual) { despesas ->
            _despesas.value = despesas
        }
    }
}
