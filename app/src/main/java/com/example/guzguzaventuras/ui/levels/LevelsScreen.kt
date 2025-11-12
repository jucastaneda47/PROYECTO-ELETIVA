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
fun LevelsScreen(navController: NavHostController) {
    BackgroundImage(R.drawable.fondo) { // fondo del menú de niveles
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ✅ TÍTULO
            Text(
                text = "NIVELES",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ GRID DE NIVELES (3x3)
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
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF3E4A8B),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable {
                                        navController.navigate("level$level")
                                    }
                                    .padding(horizontal = 30.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "LEVEL $level",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ✅ BOTÓN VOLVER (manteniendo estilo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3E4A8B), shape = RoundedCornerShape(50))
                    .clickable { navController.navigate("tio") }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VOLVER A CASA DEL TÍO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}