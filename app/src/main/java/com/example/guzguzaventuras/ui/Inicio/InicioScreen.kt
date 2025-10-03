package com.example.guzguzaventuras.ui.Inicio

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.theme.BackgroundImage

@Composable
fun InicioScreen(navController: NavHostController) {
    BackgroundImage(R.drawable.inicio) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate("tio") }) {
                Text("START")
            }
        }
    }
}