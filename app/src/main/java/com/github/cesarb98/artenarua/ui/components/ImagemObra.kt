package com.github.cesarb98.artenarua.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.cesarb98.artenarua.R
import com.github.cesarb98.artenarua.util.ImagemUtil

/** Foto da obra (drawable local) ou o placeholder quando ela não existe. */
@Composable
fun ImagemObra(
    imagemNome: String?,
    titulo: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = remember(imagemNome) { ImagemUtil.resolver(context, imagemNome) }
    Image(
        painter = painterResource(resId),
        contentDescription = stringResource(R.string.imagem_da_obra, titulo),
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
