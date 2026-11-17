package com.github.cesarb98.artenarua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cesarb98.artenarua.data.Obra
import com.github.cesarb98.artenarua.repository.ObraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ObraFormUiState(
    val titulo: String = "",
    val artista: String = "",
    val historia: String = "",
    val periodo: String = "",
    val local: String = "",
    val tecnica: String = "",
    val erroTitulo: String? = null,
    val erroArtista: String? = null,
    val erroHistoria: String? = null,
    val salvando: Boolean = false,
    val cadastrada: Boolean = false         // dispara a volta para a listagem
)

@HiltViewModel
class ObraFormViewModel @Inject constructor(
    private val repository: ObraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ObraFormUiState())
    val uiState: StateFlow<ObraFormUiState> = _uiState.asStateFlow()

    // Cada alteração respeita o limite do campo e limpa o erro daquele campo.
    fun alterarTitulo(texto: String) =
        _uiState.update { it.copy(titulo = texto.take(MAX_TITULO), erroTitulo = null) }

    fun alterarArtista(texto: String) =
        _uiState.update { it.copy(artista = texto.take(MAX_ARTISTA), erroArtista = null) }

    fun alterarHistoria(texto: String) =
        _uiState.update { it.copy(historia = texto.take(MAX_HISTORIA), erroHistoria = null) }

    fun alterarPeriodo(texto: String) =
        _uiState.update { it.copy(periodo = texto.take(MAX_PERIODO)) }

    fun alterarLocal(texto: String) =
        _uiState.update { it.copy(local = texto.take(MAX_LOCAL)) }

    fun alterarTecnica(texto: String) =
        _uiState.update { it.copy(tecnica = texto.take(MAX_TECNICA)) }

    fun salvar() {
        val estado = _uiState.value
        if (estado.salvando || estado.cadastrada) return

        val erroTitulo = ERRO_TITULO.takeIf { estado.titulo.isBlank() }
        val erroArtista = ERRO_ARTISTA.takeIf { estado.artista.isBlank() }
        val erroHistoria = ERRO_HISTORIA.takeIf { estado.historia.isBlank() }

        if (erroTitulo != null || erroArtista != null || erroHistoria != null) {
            _uiState.update {
                it.copy(
                    erroTitulo = erroTitulo,
                    erroArtista = erroArtista,
                    erroHistoria = erroHistoria
                )
            }
            return
        }

        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            repository.cadastrar(
                Obra(
                    titulo = estado.titulo.trim(),
                    artista = estado.artista.trim(),
                    historia = estado.historia.trim(),
                    periodo = estado.periodo.trim().ifEmpty { null },
                    local = estado.local.trim().ifEmpty { null },
                    tecnica = estado.tecnica.trim().ifEmpty { null },
                    imagemNome = null,
                    favorita = false,
                    comentario = null,
                    criadaPeloUsuario = true
                )
            )
            _uiState.update { it.copy(salvando = false, cadastrada = true) }
        }
    }

    companion object {
        const val MAX_TITULO = 80
        const val MAX_ARTISTA = 60
        const val MAX_HISTORIA = 1_000
        const val MAX_PERIODO = 40
        const val MAX_LOCAL = 120
        const val MAX_TECNICA = 40

        const val ERRO_TITULO = "Informe o nome da obra"
        const val ERRO_ARTISTA = "Informe o autor"
        const val ERRO_HISTORIA = "Conte a história da obra"
    }
}
