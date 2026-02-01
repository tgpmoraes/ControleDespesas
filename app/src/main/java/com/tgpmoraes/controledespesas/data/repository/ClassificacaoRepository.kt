package com.tgpmoraes.controledespesas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tgpmoraes.controledespesas.data.model.Classificacao

class ClassificacaoRepository {

    private val db = FirebaseFirestore.getInstance()

    fun listar(onResult: (List<Classificacao>) -> Unit) {
        db.collection("classificacoes")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull {
                    it.toObject(Classificacao::class.java)
                        ?.copy(id = it.id)
                }
                onResult(lista)
            }
    }
}