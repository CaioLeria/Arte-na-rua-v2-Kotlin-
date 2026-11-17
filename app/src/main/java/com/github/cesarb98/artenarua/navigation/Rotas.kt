package com.github.cesarb98.artenarua.navigation

object Rotas {
    const val HOME = "home"
    const val OBRAS = "obras"
    const val CADASTRO = "cadastro"

    const val ARG_OBRA_ID = "obraId"
    const val DETALHE = "detalhe/{$ARG_OBRA_ID}"
    fun detalhe(obraId: Long) = "detalhe/$obraId"

    /** Chave gravada no savedStateHandle da listagem quando o cadastro termina com sucesso. */
    const val RESULTADO_OBRA_CADASTRADA = "obra_cadastrada"
}
