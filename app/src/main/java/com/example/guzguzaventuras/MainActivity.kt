package com.example.guzguzaventuras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.guzguzaventuras.ui.theme.guz_guz_aventuras_theme
import com.example.guzguzaventuras.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            guz_guz_aventuras_theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = androidx.navigation.compose.rememberNavController()
                    AppNavigation(navController = navController)   // ✅ Esta sí existe
                }
            }
        }
    }
}
