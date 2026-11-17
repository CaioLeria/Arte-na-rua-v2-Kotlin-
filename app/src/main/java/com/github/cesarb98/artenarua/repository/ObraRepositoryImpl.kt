package com.github.cesarb98.artenarua.repository

import com.github.cesarb98.artenarua.data.Obra
import com.github.cesarb98.artenarua.data.ObraDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObraRepositoryImpl @Inject constructor(
    private val dao: ObraDao
) : ObraRepository {

    override fun observarTodas(): Flow<List<Obra>> = dao.observarTodas()

    override fun observarPorId(id: Long): Flow<Obra?> = dao.observarPorId(id)

    override suspend fun cadastrar(obra: Obra): Long = dao.inserir(obra)

    override suspend fun alternarFavorita(id: Long, favorita: Boolean) {
        dao.atualizarFavorita(id, favorita)
    }

    override suspend fun salvarComentario(id: Long, comentario: String?) {
        dao.atualizarComentario(id, comentario)
    }
}
