package com.github.cesarb98.artenarua.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cesarb98.artenarua.R
import com.github.cesarb98.artenarua.ui.components.EstadoVazio
import com.github.cesarb98.artenarua.ui.components.FiltroObrasChips
import com.github.cesarb98.artenarua.ui.components.ObraCard
import com.github.cesarb98.artenarua.viewmodel.FiltroObras
import com.github.cesarb98.artenarua.viewmodel.ObraListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObraListScreen(
    onAbrirObra: (Long) -> Unit,
    onCadastrar: () -> Unit,
    obraCadastrada: Boolean,
    onObraCadastradaExibida: () -> Unit,
    viewModel: ObraListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val mensagemCadastrada = stringResource(R.string.obra_cadastrada)

    // Resultado vindo do cadastro: mostra o Snackbar uma vez e limpa a chave.
    // O Snackbar roda em um escopo próprio: limpar a chave muda o parâmetro do
    // LaunchedEffect, o que cancelaria um showSnackbar chamado direto aqui dentro.
    val escopo = rememberCoroutineScope()
    LaunchedEffect(obraCadastrada) {
        if (obraCadastrada) {
            escopo.launch { snackbarHostState.showSnackbar(mensagemCadastrada) }
            onObraCadastradaExibida()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lista_titulo)) },
                actions = {
                    IconButton(onClick = onCadastrar) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.lista_cadastrar)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FiltroObrasChips(
                selecionado = uiState.filtro,
                onSelecionar = viewModel::selecionarFiltro,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            when {
                uiState.carregando -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                uiState.obras.isEmpty() && uiState.filtro == FiltroObras.FAVORITAS -> EstadoVazio(
                    icone = Icons.Outlined.FavoriteBorder,
                    mensagem = stringResource(R.string.lista_vazia_favoritas)
                )

                uiState.obras.isEmpty() -> EstadoVazio(
                    icone = Icons.Outlined.Palette,
                    mensagem = stringResource(R.string.lista_vazia_todas)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.obras, key = { it.id }) { obra ->
                        ObraCard(
                            obra = obra,
                            onClick = { onAbrirObra(obra.id) },
                            onAlternarFavorita = { viewModel.alternarFavorita(obra) }
                        )
                    }
                }
            }
        }
    }
}
