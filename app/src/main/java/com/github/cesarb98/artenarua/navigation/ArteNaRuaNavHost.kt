package com.github.cesarb98.artenarua.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.cesarb98.artenarua.R
import com.github.cesarb98.artenarua.ui.cadastro.ObraFormScreen
import com.github.cesarb98.artenarua.ui.detalhe.ObraDetailScreen
import com.github.cesarb98.artenarua.ui.home.HomeScreen
import com.github.cesarb98.artenarua.ui.lista.ObraListScreen

private data class Aba(val rota: String, val rotulo: Int, val icone: ImageVector)

private val abas = listOf(
    Aba(Rotas.HOME, R.string.aba_home, Icons.Filled.Home),
    Aba(Rotas.OBRAS, R.string.aba_obras, Icons.Filled.Palette)
)

private fun NavHostController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun ArteNaRuaNavHost() {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // A bottom bar só aparece nas duas abas principais.
            if (rotaAtual == Rotas.HOME || rotaAtual == Rotas.OBRAS) {
                NavigationBar {
                    abas.forEach { aba ->
                        NavigationBarItem(
                            selected = rotaAtual == aba.rota,
                            onClick = { navController.irParaAba(aba.rota) },
                            icon = { Icon(aba.icone, contentDescription = null) },
                            label = { Text(stringResource(aba.rotulo)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.HOME,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Rotas.HOME) {
                HomeScreen(onVerObras = { navController.irParaAba(Rotas.OBRAS) })
            }

            composable(Rotas.OBRAS) { entrada ->
                val obraCadastrada by entrada.savedStateHandle
                    .getStateFlow(Rotas.RESULTADO_OBRA_CADASTRADA, false)
                    .collectAsStateWithLifecycle()

                ObraListScreen(
                    onAbrirObra = { id -> navController.navigate(Rotas.detalhe(id)) },
                    onCadastrar = { navController.navigate(Rotas.CADASTRO) },
                    obraCadastrada = obraCadastrada,
                    onObraCadastradaExibida = {
                        entrada.savedStateHandle[Rotas.RESULTADO_OBRA_CADASTRADA] = false
                    }
                )
            }

            composable(
                route = Rotas.DETALHE,
                arguments = listOf(navArgument(Rotas.ARG_OBRA_ID) { type = NavType.LongType })
            ) {
                ObraDetailScreen(onVoltar = { navController.popBackStack() })
            }

            composable(Rotas.CADASTRO) {
                ObraFormScreen(
                    onVoltar = { navController.popBackStack() },
                    onCadastrada = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(Rotas.RESULTADO_OBRA_CADASTRADA, true)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
