package com.tgpmoraes.controledespesas.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tgpmoraes.controledespesas.data.model.Classificacao
import com.tgpmoraes.controledespesas.data.model.Despesa
import com.tgpmoraes.controledespesas.data.model.Fonte
import com.tgpmoraes.controledespesas.data.repository.DespesaRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.firebase.firestore.FirebaseFirestore
import com.tgpmoraes.controledespesas.data.model.DespesaRecorrente
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


class DespesaViewModel : ViewModel() {

    private val repository = DespesaRepository()

    private val _classificacoes = mutableStateOf<List<Classificacao>>(emptyList())
    val classificacoes: State<List<Classificacao>> = _classificacoes

    private val _fontes = mutableStateOf<List<Fonte>>(emptyList())
    val fontes: State<List<Fonte>> = _fontes

    init {
        repository.buscarClassificacoes { _classificacoes.value = it }
        repository.buscarFontes { _fontes.value = it }
    }

    fun salvarDespesa(
        descricao: String,
        valor: Double,
        parcelas: Int,
        classificacao: Classificacao?,
        fonte: Fonte?
    ) {
        val valorParcela =
            if (parcelas > 1) valor / parcelas else valor

        // Data base = agora
        val dataBase = ZonedDateTime.now(ZoneId.systemDefault())

        for (i in 0 until parcelas) {
            val dataParcela = dataBase
                .plusMonths(i.toLong())
                .toInstant()
                .toEpochMilli()

            val despesa = Despesa(
                descricao = descricao,
                valor = valorParcela,
                classificacaoId = classificacao?.id,
                classificacaoNome = classificacao?.nome,
                fonteId = fonte?.id,
                data = dataParcela,
                parcelaAtual = i + 1,
                totalParcelas = parcelas
            )

            repository.salvarDespesa(despesa)
        }
    }

    fun atualizarDespesa(
        id: String,
        descricao: String,
        valor: Double,
        classificacao: Classificacao?,
        fonte: Fonte?
    ) {
        repository.atualizarDespesa(
            id,
            mapOf(
                "descricao" to descricao,
                "valor" to valor,
                "classificacaoId" to classificacao?.id,
                "classificacaoNome" to classificacao?.nome,
                "fonteId" to fonte?.id
            )
        )
    }

    fun salvarDespesaRecorrente(
        descricao: String,
        valor: Double,
        classificacao: Classificacao?,
        fonte: Fonte?
    ) {
        val recorrenteId =
            FirebaseFirestore.getInstance()
                .collection("despesas_recorrentes")
                .document()
                .id

        val recorrente = DespesaRecorrente(
            id = recorrenteId,
            descricao = descricao,
            valor = valor,
            classificacaoId = classificacao?.id,
            classificacaoNome = classificacao?.nome,
            fonteId = fonte?.id,
            ativa = true,
            criadaEm = System.currentTimeMillis()
        )

        repository.salvarDespesaRecorrente(recorrente)

        val despesaMesAtual = Despesa(
            descricao = descricao,
            valor = valor,
            classificacaoId = classificacao?.id,
            classificacaoNome = classificacao?.nome,
            fonteId = fonte?.id,
            data = System.currentTimeMillis(),
            recorrenteId = recorrenteId
        )

        repository.salvarDespesa(despesaMesAtual)
    }

    init {
        repository.buscarClassificacoes {
            _classificacoes.value = it
        }

        repository.buscarFontes {
            _fontes.value = it
        }
    }

}