package com.github.cesarb98.artenarua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ObraDao {

    @Query("SELECT * FROM obras ORDER BY id ASC")
    fun observarTodas(): Flow<List<Obra>>

    @Query("SELECT * FROM obras WHERE id = :id")
    fun observarPorId(id: Long): Flow<Obra?>

    @Insert
    suspend fun inserir(obra: Obra): Long

    @Query("UPDATE obras SET favorita = :favorita WHERE id = :id")
    suspend fun atualizarFavorita(id: Long, favorita: Boolean): Int

    @Query("UPDATE obras SET comentario = :comentario WHERE id = :id")
    suspend fun atualizarComentario(id: Long, comentario: String?): Int
}
