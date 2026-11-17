package com.github.cesarb98.artenarua.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.cesarb98.artenarua.R
import com.github.cesarb98.artenarua.viewmodel.FiltroObras

/** Chips "Todas | Favoritas" da listagem. */
@Composable
fun FiltroObrasChips(
    selecionado: FiltroObras,
    onSelecionar: (FiltroObras) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selecionado == FiltroObras.TODAS,
            onClick = { onSelecionar(FiltroObras.TODAS) },
            label = { Text(stringResource(R.string.filtro_todas)) }
        )
        FilterChip(
            selected = selecionado == FiltroObras.FAVORITAS,
            onClick = { onSelecionar(FiltroObras.FAVORITAS) },
            label = { Text(stringResource(R.string.filtro_favoritas)) }
        )
    }
}
