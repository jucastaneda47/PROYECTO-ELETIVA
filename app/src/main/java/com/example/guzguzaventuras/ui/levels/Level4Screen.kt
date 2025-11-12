package com.example.guzguzaventuras.ui.levels

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guzguzaventuras.R
import kotlinx.coroutines.delay

@Composable
fun Level4Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp
    val screenW = config.screenWidthDp
    val context = LocalContext.current

    // 🖼️ Imágenes
    val bg = ImageBitmap.imageResource(context.resources, R.drawable.niveles)
    val piedra = ImageBitmap.imageResource(context.resources, R.drawable.piedra)
    val tronco = ImageBitmap.imageResource(context.resources, R.drawable.tronco)
    val casaTio = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)

    val dogQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val dogRight = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val dogLeft = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val dogJump = ImageBitmap.imageResource(context.resources, R.drawable.saltar)
    var dog by remember { mutableStateOf(dogQuieto) }

    // 🐶 Config jugador
    val dogSizeDp = 90.dp
    val floorY = screenH * 0.60f
    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(floorY) }
    var velocity by remember { mutableStateOf(0f) }
    var jumping by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }

    val gravity = 2.2f
    val jumpForce = -28f
    var cameraX by remember { mutableStateOf(0f) }

    // 🪨 Obstáculos fijos
    val obstacles = listOf(
        RectF(600f, floorY - 95f, 720f, floorY + 25f),   // piedra
        RectF(1200f, floorY - 95f, 1320f, floorY + 25f), // piedra
        RectF(2000f, floorY - 95f, 2120f, floorY + 25f), // piedra
        RectF(2600f, floorY - 95f, 2720f, floorY + 25f), // piedra
        RectF(3200f, floorY - 95f, 3320f, floorY + 25f)  // piedra
    )

    // 🌲 Troncos móviles
    var trunk1X by remember { mutableStateOf(1550f) } // entre piedra 2 y 3
    var trunk1Dir by remember { mutableStateOf(1) }

    var trunk2X by remember { mutableStateOf(4000f) } // antes de la meta
    var trunk2Dir by remember { mutableStateOf(1) }

    val trunkWidth = 120f
    val trunkHeight = 120f

    // 🎞️ Movimiento constante de ambos troncos
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)

            // Tronco 1
            trunk1X += trunk1Dir * 4f
            if (trunk1X < 1450f || trunk1X > 1750f) trunk1Dir *= -1

            // Tronco 2
            trunk2X += trunk2Dir * 4f
            if (trunk2X < 3900f || trunk2X > 4100f) trunk2Dir *= -1
        }
    }

    // 🏠 Meta al final del nivel
    val goalX = 4700f
    val goalWidth = 320f
    val goalHeight = 350f
    val goalRect = RectF(goalX, floorY - goalHeight + 30f, goalX + goalWidth, floorY + 30f)

    // ⚙️ Física del jugador
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (!levelCompleted) {
                // Gravedad / salto
                if (jumping) {
                    playerY += velocity
                    velocity += gravity
                    if (playerY >= floorY) {
                        playerY = floorY
                        velocity = 0f
                        jumping = false
                        dog = dogQuieto
                    }
                }

                // Cámara sigue al jugador
                cameraX = (playerX - screenW * 0.3f).coerceAtLeast(0f)

                val playerRect = RectF(
                    playerX + 10f,
                    playerY - dogSizeDp.value + 22f,
                    playerX + dogSizeDp.value - 10f,
                    playerY + 22f
                )

                // Colisiones con obstáculos
                val allObstacles = obstacles +
                        RectF(trunk1X, floorY - 95f, trunk1X + trunkWidth, floorY + 25f) +
                        RectF(trunk2X, floorY - 95f, trunk2X + trunkWidth, floorY + 25f)

                for (o in allObstacles) {
                    val overlaps = playerRect.bottom > o.top && playerRect.top < o.bottom &&
                            playerRect.right > o.left && playerRect.left < o.right

                    if (overlaps) {
                        // Desde la izquierda
                        if (playerRect.right > o.left && playerRect.left < o.left) {
                            playerX = o.left - dogSizeDp.value + 5f
                        }
                        // Desde la derecha
                        else if (playerRect.left < o.right && playerRect.right > o.right) {
                            playerX = o.right + 5f
                        }
                    }
                }

                // Meta alcanzada
                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right) {
                    levelCompleted = true
                }
            }
        }
    }

    // ✅ Regresa a la pantalla de niveles
    LaunchedEffect(levelCompleted) {
        if (levelCompleted) {
            delay(1500)
            navController.navigate("levels") {
                popUpTo("level4") { inclusive = true }
            }
        }
    }

    // 🎨 Dibujo
    Box(Modifier.fillMaxSize()) {
        // Fondo extendido
        Canvas(Modifier.fillMaxSize()) {
            val bgW = bg.width.toFloat()
            for (i in -1..8) drawImage(bg, topLeft = Offset(bgW * i - cameraX, 0f))
        }

        // Obstáculos
        obstacles.forEach {
            Image(
                bitmap = piedra,
                contentDescription = null,
                modifier = Modifier
                    .offset((it.left - cameraX).dp, it.top.dp)
                    .size(it.width().dp, it.height().dp)
            )
        }

        // Troncos móviles
        Image(
            bitmap = tronco,
            contentDescription = null,
            modifier = Modifier
                .offset((trunk1X - cameraX).dp, (floorY - 95f).dp)
                .size(trunkWidth.dp, trunkHeight.dp)
        )
        Image(
            bitmap = tronco,
            contentDescription = null,
            modifier = Modifier
                .offset((trunk2X - cameraX).dp, (floorY - 95f).dp)
                .size(trunkWidth.dp, trunkHeight.dp)
        )

        // Meta
        Image(
            bitmap = casaTio,
            contentDescription = null,
            modifier = Modifier
                .offset((goalRect.left - cameraX).dp, goalRect.top.dp)
                .size(goalWidth.dp, goalHeight.dp)
        )

        // Personaje
        Image(
            bitmap = dog,
            contentDescription = null,
            modifier = Modifier
                .offset((playerX - cameraX).dp, (playerY - dogSizeDp.value + 22f).dp)
                .size(dogSizeDp)
        )

        // Controles
        Row(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldableButton("←") { playerX -= 40f; if (!jumping) dog = dogLeft }
            HoldableButton("↑") {
                if (!jumping) { jumping = true; velocity = jumpForce; dog = dogJump }
            }
            HoldableButton("→") { playerX += 40f; if (!jumping) dog = dogRight }
        }

        // Mensaje de victoria
        if (levelCompleted)
            Box(
                modifier = Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(16.dp)
            ) {
                Text("¡Nivel completado!", color = Color.White, fontWeight = FontWeight.Bold)
            }
    }
}
