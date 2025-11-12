package com.example.guzguzaventuras.ui.levels.tio

import android.graphics.RectF
import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.levels.HoldableButton
import kotlinx.coroutines.delay

@Composable
fun Level3Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp
    val screenW = config.screenWidthDp
    val context = LocalContext.current

    // ======= Imágenes =======
    val bg = ImageBitmap.imageResource(context.resources, R.drawable.niveles)
    val rock = ImageBitmap.imageResource(context.resources, R.drawable.piedra)
    val trunk = ImageBitmap.imageResource(context.resources, R.drawable.tronco)
    val casaTio = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)

    // ======= Personaje =======
    val dogQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val dogRight = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val dogLeft = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val dogJump = ImageBitmap.imageResource(context.resources, R.drawable.saltar)
    var dog by remember { mutableStateOf(dogQuieto) }

    val dogSizeDp = 120.dp
    val floorY = screenH * 0.60f

    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(floorY) }
    var velocity by remember { mutableStateOf(0f) }
    var jumping by remember { mutableStateOf(false) }
    var cameraX by remember { mutableStateOf(0f) }
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }

    val gravity = 2.2f
    val jumpForce = -28f

    // ======= Obstáculos móviles =======
    val OB_W = 160f
    val OB_H = 120f
    val baseY = floorY - OB_H

    val obsXStart = floatArrayOf(700f, 1400f, 2100f, 2700f, 3300f)
    val obsXEnd = floatArrayOf(1000f, 1700f, 2400f, 3000f, 3600f)
    val obsX = remember { mutableStateListOf(*obsXStart.toTypedArray()) }
    val obsDir = remember { mutableStateListOf(1f, -1f, 1f, -1f, 1f) }
    val obsSpeed = 2.8f

    val goalX = 4000f
    val goalRect = RectF(goalX, floorY - 350f + 30f, goalX + 320f, floorY + 30f)

    // ======= LOOP PRINCIPAL =======
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)

            if (!dead && !completed) {
                // Física del salto
                if (jumping) {
                    playerY += velocity
                    velocity += gravity
                }
                if (playerY >= floorY) {
                    playerY = floorY
                    velocity = 0f
                    jumping = false
                    dog = dogQuieto
                }

                // Movimiento de obstáculos
                for (i in obsX.indices) {
                    val next = obsX[i] + obsDir[i] * obsSpeed
                    obsX[i] = next
                    if (next < obsXStart[i]) obsDir[i] = 1f
                    if (next > obsXEnd[i]) obsDir[i] = -1f
                }

                // Cámara
                cameraX = (playerX - screenW * 0.30f).coerceAtLeast(0f)

                // Colisiones con obstáculos
                val playerRect = RectF(
                    playerX + 10f,
                    playerY - dogSizeDp.value + 22f,
                    playerX + dogSizeDp.value - 10f,
                    playerY + 22f
                )

                for (i in obsX.indices) {
                    val oRect = RectF(obsX[i], baseY, obsX[i] + OB_W, baseY + OB_H)
                    val overlaps = playerRect.right > oRect.left &&
                            playerRect.left < oRect.right &&
                            playerRect.bottom > oRect.top &&
                            playerRect.top < oRect.bottom

                    if (overlaps) {
                        if (playerRect.centerX() < oRect.centerX()) {
                            playerX = oRect.left - (dogSizeDp.value - 10f)
                        } else {
                            playerX = oRect.right - 10f
                        }
                    }
                }

                // Meta
                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right) {
                    completed = true
                }
            } else if (dead) {
                playerY += 14f
            }
        }
    }

    // ======= Reinicios =======
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level3") {
                popUpTo("level3") { inclusive = true }
            }
        }
    }

    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels") {
                popUpTo("level3") { inclusive = true }
            }
        }
    }

    // ======= Animación de apagones rápidos =======
    val infiniteTransition = rememberInfiniteTransition(label = "darkFade")
    val darkOverlayAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing), // ⚡ más rápido (1 segundo)
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // ======= DIBUJO =======
    Box(Modifier.fillMaxSize()) {
        // Fondo
        Canvas(Modifier.fillMaxSize()) {
            val bgW = bg.width.toFloat()
            for (i in -1..10) drawImage(bg, topLeft = Offset(bgW * i - cameraX, 0f))
        }

        // Obstáculos
        obsX.forEachIndexed { i, ox ->
            val rect = RectF(ox, baseY, ox + OB_W, baseY + OB_H)
            val img = if (i % 2 == 0) rock else trunk
            Image(
                bitmap = img,
                contentDescription = "Obstáculo",
                modifier = Modifier
                    .offset((rect.left - cameraX).dp, rect.top.dp)
                    .size(rect.width().dp, rect.height().dp)
            )
        }

        // Meta
        Image(
            bitmap = casaTio,
            contentDescription = "Meta",
            modifier = Modifier
                .offset((goalRect.left - cameraX).dp, goalRect.top.dp)
                .size(320.dp, 350.dp)
        )

        // Personaje
        Image(
            bitmap = dog,
            contentDescription = null,
            modifier = Modifier
                .offset((playerX - cameraX).dp, (playerY - dogSizeDp.value + 20f).dp)
                .size(dogSizeDp)
        )

        // 🌑 Oscuridad intermitente rápida
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Black.copy(alpha = darkOverlayAlpha),
                size = Size(size.width, size.height)
            )
        }

        // Controles
        Row(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldableButton("←") { playerX -= 40f; if (!jumping) dog = dogLeft }
            HoldableButton("↑") {
                if (!jumping) {
                    jumping = true; velocity = -28f; dog = dogJump
                }
            }
            HoldableButton("→") { playerX += 40f; if (!jumping) dog = dogRight }
        }

        // Mensajes
        if (completed)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(16.dp)
            ) { Text("¡Nivel completado!", color = Color.White, fontWeight = FontWeight.Bold) }

        if (dead)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(16.dp)
            ) { Text("¡Has sido atrapado en la oscuridad!", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}