package com.github.cesarb98.artenarua.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cesarb98.artenarua.data.Obra
import com.github.cesarb98.artenarua.navigation.Rotas
import com.github.cesarb98.artenarua.repository.ObraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ObraDetailUiState(
    val obra: Obra? = null,
    val carregando: Boolean = true,
    val editandoComentario: Boolean = false,
    val rascunhoComentario: String = "",
    val erroComentario: String? = null,
    val mensagem: String? = null            // para Snackbar (consumida pela View)
) {
    /** Salvar só com texto não vazio (após trim) e dentro do limite. */
    val podeSalvarComentario: Boolean
        get() = rascunhoComentario.isNotBlank() && erroComentario == null
}

@HiltViewModel
class ObraDetailViewModel @Inject constructor(
    private val repository: ObraRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val obraId: Long = savedStateHandle[Rotas.ARG_OBRA_ID] ?: -1L

    private val _uiState = MutableStateFlow(ObraDetailUiState())
    val uiState: StateFlow<ObraDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observarPorId(obraId).collect { obra ->
                _uiState.update { it.copy(obra = obra, carregando = false) }
            }
        }
    }

    fun alternarFavorita() {
        val obra = _uiState.value.obra ?: return
        viewModelScope.launch { repository.alternarFavorita(obra.id, !obra.favorita) }
    }

    fun iniciarEdicaoComentario() {
        _uiState.update {
            it.copy(
                editandoComentario = true,
                rascunhoComentario = it.obra?.comentario.orEmpty(),
                erroComentario = null
            )
        }
    }

    fun alterarRascunho(texto: String) {
        _uiState.update {
            it.copy(
                rascunhoComentario = texto,
                erroComentario = if (texto.trim().length > LIMITE_COMENTARIO) ERRO_LIMITE else null
            )
        }
    }

    fun salvarComentario() {
        val estado = _uiState.value
        val obra = estado.obra ?: return
        if (!estado.podeSalvarComentario) return
        viewModelScope.launch {
            repository.salvarComentario(obra.id, estado.rascunhoComentario.trim())
            _uiState.update {
                it.copy(
                    editandoComentario = false,
                    rascunhoComentario = "",
                    erroComentario = null,
                    mensagem = MSG_COMENTARIO_SALVO
                )
            }
        }
    }

    fun cancelarEdicao() {
        _uiState.update {
            it.copy(editandoComentario = false, rascunhoComentario = "", erroComentario = null)
        }
    }

    fun removerComentario() {
        val obra = _uiState.value.obra ?: return
        viewModelScope.launch {
            repository.salvarComentario(obra.id, null)
            _uiState.update {
                it.copy(
                    editandoComentario = false,
                    rascunhoComentario = "",
                    erroComentario = null,
                    mensagem = MSG_COMENTARIO_REMOVIDO
                )
            }
        }
    }

    fun mensagemExibida() {
        _uiState.update { it.copy(mensagem = null) }
    }

    companion object {
        const val LIMITE_COMENTARIO = 500
        const val ERRO_LIMITE = "O comentário pode ter no máximo 500 caracteres"
        const val MSG_COMENTARIO_SALVO = "Comentário salvo"
        const val MSG_COMENTARIO_REMOVIDO = "Comentário removido"
    }
}
