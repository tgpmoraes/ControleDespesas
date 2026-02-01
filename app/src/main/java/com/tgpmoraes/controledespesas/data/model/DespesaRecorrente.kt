package com.tgpmoraes.controledespesas.data.model

data class DespesaRecorrente(
    val id: String = "",
    val descricao: String = "",
    val classificacaoId: String? = null,
    val classificacaoNome: String? = null,
    val fonteId: String? = null,
    val valor: Double = 0.0,
    val diaVencimento: Int = 1, // ex: dia 5
    val ativa: Boolean = true,
    val criadaEm: Long = 0L
)