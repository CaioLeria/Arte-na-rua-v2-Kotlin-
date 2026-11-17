package com.github.cesarb98.artenarua.ui.detalhe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.cesarb98.artenarua.R

@Composable
fun ObraDetailScreen(onVoltar: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onVoltar) { Text(stringResource(R.string.voltar)) }
    }
}
