package com.example.guzguzaventuras.ui.levels

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.theme.BackgroundImage

@Composable
fun LevelsScreen(navController: NavHostController) {
    BackgroundImage(R.drawable.fondo) { // 👉 usa el fondo que quieras
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Título arriba
            Text(
                text = "NIVELES",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid de niveles (3 columnas x 3 filas)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 1..3) {
                            val level = row * 3 + col
                            Button(
                                onClick = {
                                    if (level == 1) {
                                        // Solo el nivel 1 regresa a Casa del Tío
                                        navController.navigate("tio")
                                    }
                                },
                                enabled = (level == 1) // Solo habilitado el nivel 1
                            ) {
                                Text("LEVEL $level")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Botón VOLVER abajo
            Button(
                onClick = { navController.navigate("tio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text("VOLVER A CASA DEL TÍO")
            }
        }
    }
}