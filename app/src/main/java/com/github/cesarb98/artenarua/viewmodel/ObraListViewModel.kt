package com.github.cesarb98.artenarua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cesarb98.artenarua.data.Obra
import com.github.cesarb98.artenarua.repository.ObraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FiltroObras { TODAS, FAVORITAS }

data class ObraListUiState(
    val obras: List<Obra> = emptyList(),   // já filtradas
    val filtro: FiltroObras = FiltroObras.TODAS,
    val carregando: Boolean = true
)

@HiltViewModel
class ObraListViewModel @Inject constructor(
    private val repository: ObraRepository
) : ViewModel() {

    private val filtro = MutableStateFlow(FiltroObras.TODAS)

    val uiState: StateFlow<ObraListUiState> =
        combine(repository.observarTodas(), filtro) { obras, filtroAtual ->
            ObraListUiState(
                obras = when (filtroAtual) {
                    FiltroObras.TODAS -> obras
                    FiltroObras.FAVORITAS -> obras.filter { it.favorita }
                },
                filtro = filtroAtual,
                carregando = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ObraListUiState())

    fun selecionarFiltro(novoFiltro: FiltroObras) {
        filtro.value = novoFiltro
    }

    fun alternarFavorita(obra: Obra) {
        viewModelScope.launch {
            repository.alternarFavorita(obra.id, !obra.favorita)
        }
    }
}
