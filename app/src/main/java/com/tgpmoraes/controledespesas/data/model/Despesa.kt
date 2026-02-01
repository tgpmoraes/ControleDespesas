package com.tgpmoraes.controledespesas.data.model

data class Despesa(
    val id: String = "",
    val descricao: String = "",
    val classificacaoId: String? = null,
    val classificacaoNome: String? = null,
    val fonteId: String? = null,
    val valor: Double = 0.0,
    val data: Long = 0L,
    val parcelaAtual: Int = 1,
    val totalParcelas: Int = 1,
    val recorrenteId: String? = null
)