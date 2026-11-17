package com.github.cesarb98.artenarua.repository

import com.github.cesarb98.artenarua.data.Obra
import kotlinx.coroutines.flow.Flow

interface ObraRepository {
    fun observarTodas(): Flow<List<Obra>>
    fun observarPorId(id: Long): Flow<Obra?>
    suspend fun cadastrar(obra: Obra): Long
    suspend fun alternarFavorita(id: Long, favorita: Boolean)
    suspend fun salvarComentario(id: Long, comentario: String?)
}
