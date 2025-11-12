package com.example.guzguzaventuras.ui.levels_wasa

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.theme.BackgroundImage

@Composable
fun Levels2Screen(navController: NavHostController) {
    BackgroundImage(R.drawable.fondo_wasawasa) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Título
            Text(
                text = "NIVELES",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Solo 4 niveles (5 al 8) en grid 2x2
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rutas = listOf("level5", "level6", "level7", "level8") // ✅ rutas que SÍ existen
                rutas.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        fila.forEach { route ->
                            val etiqueta = route.filter { it.isDigit() } // "5", "6", "7", "8"
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF3E4A8B),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable { navController.navigate(route) } // ✅ navega a rutas válidas
                                    .padding(horizontal = 40.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    text = "LEVEL $etiqueta",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Volver a WasaWasa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3E4A8B), shape = RoundedCornerShape(50))
                    .clickable { navController.navigate("bar") }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VOLVER A WASA WASA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}