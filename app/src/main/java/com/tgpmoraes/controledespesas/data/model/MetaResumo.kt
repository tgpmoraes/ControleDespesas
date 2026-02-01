package com.tgpmoraes.controledespesas.data.model

data class MetaResumo(
    val classificacaoNome: String,
    val metaMensal: Double,
    val totalGasto: Double
) {
    val percentual: Double
        get() = if (metaMensal > 0)
            (totalGasto / metaMensal)
        else 0.0
}
