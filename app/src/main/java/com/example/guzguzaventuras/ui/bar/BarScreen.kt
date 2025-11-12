package com.example.guzguzaventuras.ui.bar

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
fun BarScreen(navController: NavController) {
    BackgroundImage(R.drawable.fondo_wasawasa) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 🌴 TÍTULO ARRIBA
            Text(
                "WASA WASA",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )

            // 🏠 BOTÓN CENTRAL DE INICIO
            Button(
                onClick = { navController.navigate("levels2") }, // ✅ ahora navega al menú correcto
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Text("INICIO")
            }

            // 🔁 FILA INFERIOR CON ANTERIOR Y SIGUIENTE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 🔙 Botón para volver al mundo anterior (Casa del Tío)
                Button(onClick = { navController.navigate("tio") }) {
                    Text("ANTERIOR")
                }

                // 🔜 Botón para ir al siguiente mundo (Nightclub)
                Button(onClick = { navController.navigate("nightclub") }) {
                    Text("SIGUIENTE")
                }
            }
        }
    }
}