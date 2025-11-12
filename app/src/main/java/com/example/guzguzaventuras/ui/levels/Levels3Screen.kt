package com.example.guzguzaventuras.ui.levels

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
fun Levels3Screen(navController: NavHostController) {
    BackgroundImage(R.drawable.fondo_dakiti) { // puedes cambiar el fondo si tienes otro
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ✅ Título
            Text(
                text = "NIVELES NIGHTCLUB",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Grid de niveles (2x2)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rutas = listOf("level9", "level10", "level11", "level12")
                rutas.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        fila.forEach { route ->
                            val etiqueta = route.filter { it.isDigit() } // "9", "10", etc.
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF3E4A8B), // ✅ mismo color que los otros mundos
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable { navController.navigate(route) }
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

            // ✅ Botón inferior — volver al mundo Nightclub
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3E4A8B), shape = RoundedCornerShape(50))
                    .clickable { navController.navigate("nightclub") }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VOLVER A NIGHTCLUB",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}