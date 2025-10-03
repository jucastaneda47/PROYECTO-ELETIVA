package com.example.guzguzaventuras.ui.tio

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.theme.BackgroundImage

@Composable
fun TioScreen(navController: NavController) {
    com.example.guzguzaventuras.ui.theme.BackgroundImage(R.drawable.fondo_casa_tio) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Título arriba
            Text(
                "CASA DEL TÍO",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )

            // Botón centrado (INICIO) — lleva a la pantalla de niveles
            Button(
                onClick = { navController.navigate("levels") },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Text("INICIO")
            }

            // Fila inferior con ANTERIOR (izquierda) y SIGUIENTE (derecha)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { navController.navigate("inicio") }) {
                    Text("ANTERIOR")
                }
                Button(onClick = { navController.navigate("bar") }) {
                    Text("SIGUIENTE")
                }
            }
        }
    }
}
