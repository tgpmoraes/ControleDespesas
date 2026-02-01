package com.tgpmoraes.controledespesas.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.tgpmoraes.controledespesas.data.model.Classificacao
import com.tgpmoraes.controledespesas.data.model.Despesa
import com.tgpmoraes.controledespesas.data.model.DespesaRecorrente
import com.tgpmoraes.controledespesas.data.model.Fonte

class DespesaRepository {

    private val db = FirebaseFirestore.getInstance()

    fun salvarDespesa(despesa: Despesa) {
        val docRef = FirebaseFirestore.getInstance()
            .collection("despesas")
            .document()

        val despesaComId = despesa.copy(id = docRef.id)

        docRef.set(despesaComId)
    }

    fun buscarClassificacoes(onResult: (List<Classificacao>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("classificacoes")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("Firestore", "Erro", error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.map {
                    Classificacao(
                        id = it.id,
                        nome = it.getString("nome") ?: "",
                        metaMensal = it.getDouble("metaMensal") ?: 0.0
                    )
                } ?: emptyList()

                Log.d("Firestore", "Recebidas ${lista.size} classificações")

                onResult(lista)
            }
    }

    fun buscarFontes(onResult: (List<Fonte>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("fontes")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("Firestore", "Erro", error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.map {
                    Fonte(
                        id = it.id,
                        nome = it.getString("nome") ?: "",
                        diaInicioCiclo = it.getLong("diaInicioCiclo")?.toInt()
                    )
                } ?: emptyList()

                Log.d(
                    "Firestore",
                    "Fontes carregadas: ${lista.map { "${it.nome}(${it.diaInicioCiclo})" }}"
                )

                onResult(lista)
            }
    }
    fun buscarDespesasDoPeriodo(
        inicio: Long,
        fim: Long,
        callback: (List<Despesa>) -> Unit
    ) {
        db.collection("despesas")
            .whereGreaterThanOrEqualTo("data", inicio)
            .whereLessThan("data", fim)
            .orderBy("data")
            .get()
            .addOnSuccessListener { snapshot ->

                val despesas = snapshot.documents.map { doc ->
                    Despesa(
                        id = doc.id,
                        descricao = doc.getString("descricao") ?: "",
                        classificacaoId = doc.getString("classificacaoId"),
                        classificacaoNome = doc.getString("classificacaoNome"),
                        fonteId = doc.getString("fonteId"),
                        valor = doc.getDouble("valor") ?: 0.0,
                        data = doc.getLong("data") ?: 0L,
                        parcelaAtual = (doc.getLong("parcelaAtual") ?: 1L).toInt(),
                        totalParcelas = (doc.getLong("totalParcelas") ?: 1L).toInt(),
                        recorrenteId = doc.getString("recorrenteId")
                    )
                }

                callback(despesas)
            }
    }

    fun excluirDespesa(id: String) {
        FirebaseFirestore.getInstance()
            .collection("despesas")
            .document(id)
            .delete()
    }

    fun excluirDespesaRecorrente(id: String) {
        FirebaseFirestore.getInstance()
            .collection("despesas_recorrentes")
            .document(id)
            .delete()
    }

    fun atualizarDespesa(id: String, dados: Map<String, Any?>) {
        FirebaseFirestore.getInstance()
            .collection("despesas")
            .document(id)
            .update(dados)
    }

    fun salvarDespesaRecorrente(
        recorrente: DespesaRecorrente
    ) {
        FirebaseFirestore.getInstance()
            .collection("despesas_recorrentes")
            .document(recorrente.id)
            .set(recorrente)
    }


    fun atualizarDespesaRecorrente(
        id: String,
        dados: Map<String, Any?>
    ) {
        db
            .collection("despesas_recorrentes")
            .document(id)
            .update(dados)
    }

    fun buscarDespesasRecorrentes(
        callback: (List<DespesaRecorrente>) -> Unit
    ) {
        db
            .collection("despesas_recorrentes")
            .whereEqualTo("ativa", true)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull {
                    it.toObject(DespesaRecorrente::class.java)
                }
                callback(lista)
            }
    }
}