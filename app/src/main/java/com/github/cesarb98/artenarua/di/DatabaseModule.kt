package com.github.cesarb98.artenarua.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.cesarb98.artenarua.data.AppDatabase
import com.github.cesarb98.artenarua.data.ObraDao
import com.github.cesarb98.artenarua.data.ObraSeed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        // Provider evita a dependência circular: o DAO só é pedido depois do banco existir.
        obraDao: Provider<ObraDao>
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NOME)
            .addCallback(object : RoomDatabase.Callback() {
                // onCreate roda só na criação do banco (primeira abertura do app).
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        val dao = obraDao.get()
                        ObraSeed.obras.forEach { dao.inserir(it) }
                    }
                }
            })
            .build()

    @Provides
    fun provideObraDao(database: AppDatabase): ObraDao = database.obraDao()
}
