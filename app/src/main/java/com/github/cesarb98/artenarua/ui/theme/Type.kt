package com.github.cesarb98.artenarua.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val padrao = Typography()

val Tipografia = Typography(
    headlineLarge = padrao.headlineLarge.copy(fontWeight = FontWeight.Bold),
    headlineSmall = padrao.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = padrao.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = padrao.titleMedium.copy(fontWeight = FontWeight.SemiBold)
)
