package com.github.cesarb98.artenarua.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Obra::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun obraDao(): ObraDao

    companion object {
        const val NOME = "arte_na_rua.db"
    }
}
