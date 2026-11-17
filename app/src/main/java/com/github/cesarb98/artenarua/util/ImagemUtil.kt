package com.github.cesarb98.artenarua.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import com.github.cesarb98.artenarua.R

object ImagemUtil {

    /**
     * Resolve o nome de um drawable para o id do recurso. Se o nome for nulo ou o
     * recurso não existir, devolve o placeholder — assim o app roda antes das fotos
     * reais estarem em `res/drawable-nodpi/`.
     */
    @SuppressLint("DiscouragedApi")
    @DrawableRes
    fun resolver(context: Context, imagemNome: String?): Int {
        if (imagemNome.isNullOrBlank()) return R.drawable.obra_placeholder
        val id = context.resources.getIdentifier(imagemNome, "drawable", context.packageName)
        return if (id != 0) id else R.drawable.obra_placeholder
    }
}
