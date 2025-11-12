package com.example.guzguzaventuras.ui.nightclub

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.theme.BackgroundImage

@Composable
fun NightclubScreen(navController: NavController) {
    BackgroundImage(R.drawable.fondo_dakiti) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 🌃 TÍTULO ARRIBA
            Text(
                "DAKITI - NIGHTCLUB",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )

            // 🏠 BOTÓN CENTRAL DE INICIO (lleva al menú del tercer mundo)
            Button(
                onClick = { navController.navigate("levels3") }, // ✅ Menú del tercer mundo
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Text("INICIO")
            }

            // 🔁 FILA INFERIOR CON ANTERIOR Y MENÚ PRINCIPAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 🔙 Botón para volver al mundo anterior (Wasa Wasa)
                Button(onClick = { navController.navigate("bar") }) {
                    Text("ANTERIOR")
                }

                // 🏠 Botón para volver al menú principal
                Button(onClick = { navController.navigate("inicio") }) {
                    Text("MENÚ PRINCIPAL")
                }
            }
        }
    }
}