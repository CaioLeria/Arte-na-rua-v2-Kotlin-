package com.github.cesarb98.artenarua.ui.cadastro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.cesarb98.artenarua.R
import com.github.cesarb98.artenarua.viewmodel.ObraFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObraFormScreen(
    onVoltar: () -> Unit,
    onCadastrada: () -> Unit,
    viewModel: ObraFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.cadastrada) {
        if (uiState.cadastrada) onCadastrada()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cadastro_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Campo(
                valor = uiState.titulo,
                onAlterar = viewModel::alterarTitulo,
                rotulo = stringResource(R.string.campo_titulo),
                erro = uiState.erroTitulo
            )
            Campo(
                valor = uiState.artista,
                onAlterar = viewModel::alterarArtista,
                rotulo = stringResource(R.string.campo_artista),
                erro = uiState.erroArtista
            )
            Campo(
                valor = uiState.historia,
                onAlterar = viewModel::alterarHistoria,
                rotulo = stringResource(R.string.campo_historia),
                erro = uiState.erroHistoria,
                linhasMinimas = 4
            )
            Campo(
                valor = uiState.periodo,
                onAlterar = viewModel::alterarPeriodo,
                rotulo = stringResource(R.string.campo_periodo)
            )
            Campo(
                valor = uiState.local,
                onAlterar = viewModel::alterarLocal,
                rotulo = stringResource(R.string.campo_local)
            )
            Campo(
                valor = uiState.tecnica,
                onAlterar = viewModel::alterarTecnica,
                rotulo = stringResource(R.string.campo_tecnica)
            )
            Text(
                stringResource(R.string.cadastro_obrigatorios),
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = viewModel::salvar,
                enabled = !uiState.salvando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp)
            ) {
                Text(stringResource(R.string.salvar))
            }
        }
    }
}

@Composable
private fun Campo(
    valor: String,
    onAlterar: (String) -> Unit,
    rotulo: String,
    erro: String? = null,
    linhasMinimas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onAlterar,
        label = { Text(rotulo) },
        isError = erro != null,
        supportingText = { Text(erro.orEmpty()) },
        singleLine = linhasMinimas == 1,
        minLines = linhasMinimas,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = if (linhasMinimas == 1) ImeAction.Next else ImeAction.Default
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
