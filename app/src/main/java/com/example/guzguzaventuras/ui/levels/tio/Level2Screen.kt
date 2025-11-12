package com.example.guzguzaventuras.ui.levels.tio

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.levels.HoldableButton
import kotlinx.coroutines.delay

@Composable
fun Level2Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp
    val screenW = config.screenWidthDp
    val context = LocalContext.current

    // Fondo con hueco dibujado
    val bg = ImageBitmap.imageResource(context.resources, R.drawable.hueco)

    // Personaje y meta
    val casaTio   = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)
    val dogQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val dogRight  = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val dogLeft   = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val dogJump   = ImageBitmap.imageResource(context.resources, R.drawable.saltar)

    var dog by remember { mutableStateOf(dogQuieto) }

    // 🔼 Aumentamos el tamaño del perro para hacerlo más visible
    val dogSizeDp = 120.dp
    val floorY = screenH * 0.60f

    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(floorY) }
    var velocity by remember { mutableStateOf(0f) }
    var jumping  by remember { mutableStateOf(false) }

    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    var cameraX by remember { mutableStateOf(0f) }

    val gravity   = 2.2f
    val jumpForce = -28f

    // ==== HUECOS ALINEADOS AL SPRITE ====
    data class Hole(val rect: RectF)

    val HOLE_LEFT_RATIO  = 0.17f
    val HOLE_RIGHT_RATIO = 0.28f
    val HOLE_DEPTH       = 230f

    val holeTileIndices = listOf(0, 1, 2, 3, 4)

    val tileW = bg.width.toFloat()
    val holeLeftInTile  = tileW * HOLE_LEFT_RATIO
    val holeRightInTile = tileW * HOLE_RIGHT_RATIO
    val holeWidth       = holeRightInTile - holeLeftInTile

    val holes: List<Hole> = remember(tileW, floorY) {
        holeTileIndices.map { idx ->
            val left = idx * tileW + holeLeftInTile
            Hole(RectF(left, floorY, left + holeWidth, floorY + HOLE_DEPTH))
        }
    }

    val afterLast = (holeTileIndices.maxOrNull() ?: 0) + 1.2f
    val goalX     = afterLast * tileW
    val goalRect  = RectF(goalX, floorY - 350f + 30f, goalX + 320f, floorY + 30f)

    // ================= LOOP =================
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)

            if (!dead && !completed) {
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

                cameraX = (playerX - screenW * 0.30f).coerceAtLeast(0f)

                val playerRect = RectF(
                    playerX + 10f,
                    playerY - dogSizeDp.value + 22f,
                    playerX + dogSizeDp.value - 10f,
                    playerY + 22f
                )

                val centerX = playerRect.centerX()
                val isOverHole = holes.any { h -> centerX >= h.rect.left && centerX <= h.rect.right }

                if (isOverHole && playerY >= floorY && !jumping) {
                    dead = true
                    jumping = true
                    velocity = 2f
                }

                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right) {
                    completed = true
                }
            } else if (dead) {
                playerY += 14f
            }
        }
    }

    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level2") {
                popUpTo("level2") { inclusive = true }
            }
        }
    }

    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels") {
                popUpTo("level2") { inclusive = true }
            }
        }
    }

    // ================= DIBUJO =================
    Box(Modifier.fillMaxSize()) {

        Canvas(Modifier.fillMaxSize()) {
            val bgW = bg.width.toFloat()
            for (i in -1..5) {
                drawImage(image = bg, topLeft = Offset(bgW * i - cameraX, 0f))
            }
        }

        Image(
            bitmap = casaTio,
            contentDescription = "Meta",
            modifier = Modifier
                .offset((goalRect.left - cameraX).dp, goalRect.top.dp)
                .size(320.dp, 350.dp)
        )

        // 🔼 Perro más grande y más alto sobre el pasto
        Image(
            bitmap = dog,
            contentDescription = null,
            modifier = Modifier
                .offset((playerX - cameraX).dp, (playerY - dogSizeDp.value - 14f).dp)
                .size(dogSizeDp)
        )

        Row(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldableButton("←") { playerX -= 40f; if (!jumping) dog = dogLeft }
            HoldableButton("↑") {
                if (!jumping) {
                    jumping = true; velocity = jumpForce; dog = dogJump
                }
            }
            HoldableButton("→") { playerX += 40f; if (!jumping) dog = dogRight }
        }

        if (completed) {
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) { Text("¡Nivel completado!", color = Color.White) }
        }
        if (dead) {
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) { Text("¡Caíste en un agujero!", color = Color.White) }
        }
    }
}