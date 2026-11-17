package com.github.cesarb98.artenarua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obras")
data class Obra(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val artista: String,
    val historia: String,
    val periodo: String? = null,
    val local: String? = null,
    val tecnica: String? = null,
    val imagemNome: String? = null,
    val favorita: Boolean = false,
    val comentario: String? = null,
    val criadaPeloUsuario: Boolean = false
)
