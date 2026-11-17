package com.github.cesarb98.artenarua.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Tema claro fixo (sem cores dinâmicas e sem tema escuro), conforme a spec.
private val EsquemaDeCores = lightColorScheme(
    primary = OxidizedIron,
    onPrimary = BeigeClaro,
    primaryContainer = AshGrey,
    onPrimaryContainer = PitchBlack,
    secondary = OxidizedIron,
    onSecondary = BeigeClaro,
    secondaryContainer = AshGrey,
    onSecondaryContainer = PitchBlack,
    tertiary = OxidizedIron,
    onTertiary = BeigeClaro,
    background = BeigeClaro,
    onBackground = PitchBlack,
    surface = Beige,
    onSurface = PitchBlack,
    surfaceVariant = Beige,
    onSurfaceVariant = PitchBlack,
    surfaceContainerLowest = BeigeClaro,
    surfaceContainerLow = Beige,
    surfaceContainer = Beige,
    surfaceContainerHigh = Beige,
    surfaceContainerHighest = Beige,
    outline = PitchBlack.copy(alpha = 0.6f),
    outlineVariant = AshGrey,
    error = OxidizedIron,
    onError = BeigeClaro
)

@Composable
fun ArteNaRuaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaDeCores,
        typography = Tipografia,
        content = content
    )
}
