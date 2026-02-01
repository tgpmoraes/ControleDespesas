package com.tgpmoraes.controledespesas.domain.util

import java.util.Calendar

object DateUtils {

    fun calcularPeriodoCiclo(
        diaInicio: Int,
        ano: Int,
        mes: Int // 1..12
    ): Pair<Long, Long> {

        val fim = Calendar.getInstance().apply {
            set(Calendar.YEAR, ano)
            set(Calendar.MONTH, mes - 1) // Calendar é 0-based
            set(Calendar.DAY_OF_MONTH, diaInicio)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val inicio = Calendar.getInstance().apply {
            timeInMillis = fim.timeInMillis
            add(Calendar.MONTH, -1)
        }

        return Pair(inicio.timeInMillis, fim.timeInMillis)
    }


    fun periodoMes(ano: Int, mes: Int): Pair<Long, Long> {
        val inicio = Calendar.getInstance()
        val fim = Calendar.getInstance()

        inicio.set(ano, mes - 1, 1, 0, 0, 0)
        inicio.set(Calendar.MILLISECOND, 0)

        fim.timeInMillis = inicio.timeInMillis
        fim.add(Calendar.MONTH, 1)

        return Pair(inicio.timeInMillis, fim.timeInMillis)
    }
}
