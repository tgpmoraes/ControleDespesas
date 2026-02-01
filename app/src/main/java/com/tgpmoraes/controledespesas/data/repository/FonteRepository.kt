package com.tgpmoraes.controledespesas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tgpmoraes.controledespesas.data.model.Fonte

class FonteRepository {

    private val db = FirebaseFirestore.getInstance()

    fun listar(onResult: (List<Fonte>) -> Unit) {
        db.collection("fontes")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull {
                    it.toObject(Fonte::class.java)
                        ?.copy(id = it.id)
                }
                onResult(lista)
            }
    }
}