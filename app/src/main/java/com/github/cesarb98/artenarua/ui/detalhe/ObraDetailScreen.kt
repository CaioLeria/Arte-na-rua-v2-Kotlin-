package com.github.cesarb98.artenarua.ui.detalhe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cesarb98.artenarua.R
import com.github.cesarb98.artenarua.data.Obra
import com.github.cesarb98.artenarua.ui.components.BotaoFavoritar
import com.github.cesarb98.artenarua.ui.components.ImagemObra
import com.github.cesarb98.artenarua.viewmodel.ObraDetailUiState
import com.github.cesarb98.artenarua.viewmodel.ObraDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObraDetailScreen(
    onVoltar: () -> Unit,
    viewModel: ObraDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val escopo = rememberCoroutineScope()

    // O Snackbar roda em um escopo próprio: consumir a mensagem muda a chave do
    // LaunchedEffect, o que cancelaria um showSnackbar chamado direto aqui dentro.
    LaunchedEffect(uiState.mensagem) {
        uiState.mensagem?.let { mensagem ->
            escopo.launch { snackbarHostState.showSnackbar(mensagem) }
            viewModel.mensagemExibida()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.obra?.titulo.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                },
                actions = {
                    uiState.obra?.let { obra ->
                        BotaoFavoritar(
                            favorita = obra.favorita,
                            onClick = viewModel::alternarFavorita
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val obra = uiState.obra
        when {
            uiState.carregando -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                Alignment.Center
            ) { CircularProgressIndicator() }

            obra == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.detalhe_nao_encontrada),
                    style = MaterialTheme.typography.titleMedium
                )
                Button(onClick = onVoltar) { Text(stringResource(R.string.voltar)) }
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImagemObra(
                    imagemNome = obra.imagemNome,
                    titulo = obra.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                BlocoAObra(obra)
                Bloco(titulo = stringResource(R.string.detalhe_o_artista)) {
                    Text(obra.artista, style = MaterialTheme.typography.bodyLarge)
                }
                Bloco(titulo = stringResource(R.string.detalhe_historia)) {
                    Text(obra.historia, style = MaterialTheme.typography.bodyLarge)
                }
                BlocoComentario(
                    uiState = uiState,
                    comentario = obra.comentario,
                    onAdicionarOuEditar = viewModel::iniciarEdicaoComentario,
                    onAlterarRascunho = viewModel::alterarRascunho,
                    onSalvar = viewModel::salvarComentario,
                    onCancelar = viewModel::cancelarEdicao,
                    onRemover = viewModel::removerComentario
                )
            }
        }
    }
}

@Composable
private fun Bloco(titulo: String, conteudo: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            conteudo()
        }
    }
}

@Composable
private fun BlocoAObra(obra: Obra) {
    Bloco(titulo = stringResource(R.string.detalhe_a_obra)) {
        Text(obra.titulo, style = MaterialTheme.typography.titleLarge)
        obra.tecnica?.takeIf { it.isNotBlank() }?.let {
            Informacao(stringResource(R.string.detalhe_tecnica), it)
        }
        obra.periodo?.takeIf { it.isNotBlank() }?.let {
            Informacao(stringResource(R.string.detalhe_periodo), it)
        }
        obra.local?.takeIf { it.isNotBlank() }?.let {
            Informacao(stringResource(R.string.detalhe_local), it)
        }
    }
}

@Composable
private fun Informacao(rotulo: String, valor: String) {
    Column {
        Text(rotulo, style = MaterialTheme.typography.labelLarge)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun BlocoComentario(
    uiState: ObraDetailUiState,
    comentario: String?,
    onAdicionarOuEditar: () -> Unit,
    onAlterarRascunho: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
    onRemover: () -> Unit
) {
    Bloco(titulo = stringResource(R.string.detalhe_meu_comentario)) {
        when {
            uiState.editandoComentario -> {
                OutlinedTextField(
                    value = uiState.rascunhoComentario,
                    onValueChange = onAlterarRascunho,
                    label = { Text(stringResource(R.string.detalhe_campo_comentario)) },
                    isError = uiState.erroComentario != null,
                    supportingText = {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = uiState.erroComentario.orEmpty(),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                stringResource(
                                    R.string.contador_caracteres,
                                    uiState.rascunhoComentario.trim().length,
                                    ObraDetailViewModel.LIMITE_COMENTARIO
                                )
                            )
                        }
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onCancelar) { Text(stringResource(R.string.cancelar)) }
                    Button(onClick = onSalvar, enabled = uiState.podeSalvarComentario) {
                        Text(stringResource(R.string.salvar))
                    }
                }
            }

            comentario.isNullOrBlank() -> {
                Text(
                    stringResource(R.string.detalhe_sem_comentario),
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onAdicionarOuEditar) {
                    Text(stringResource(R.string.detalhe_adicionar_comentario))
                }
            }

            else -> {
                Text(comentario, style = MaterialTheme.typography.bodyLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onAdicionarOuEditar) {
                        Text(stringResource(R.string.editar))
                    }
                    TextButton(onClick = onRemover) { Text(stringResource(R.string.remover)) }
                }
            }
        }
    }
}
