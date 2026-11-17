package com.github.cesarb98.artenarua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.cesarb98.artenarua.navigation.ArteNaRuaNavHost
import com.github.cesarb98.artenarua.ui.theme.ArteNaRuaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArteNaRuaTheme {
                ArteNaRuaNavHost()
            }
        }
    }
}
