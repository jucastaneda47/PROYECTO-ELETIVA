package com.example.guzguzaventuras.ui.levels

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.guzguzaventuras.R
import kotlinx.coroutines.delay

@Composable
fun Level1Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp
    val screenW = config.screenWidthDp
    val context = LocalContext.current

    val bg = ImageBitmap.imageResource(context.resources, R.drawable.niveles)
    val piedra = ImageBitmap.imageResource(context.resources, R.drawable.piedra)
    val tronco = ImageBitmap.imageResource(context.resources, R.drawable.tronco)
    val puas = ImageBitmap.imageResource(context.resources, R.drawable.puas)
    val casaTio = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)
    val dogQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val dogRight = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val dogLeft = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val dogJump = ImageBitmap.imageResource(context.resources, R.drawable.saltar)

    var dog by remember { mutableStateOf(dogQuieto) }

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

    val obstacleWidth = 120f
    val obstacleHeight = 120f
    data class Obstacle(val rect: RectF, val image: ImageBitmap)
    val obstacles = listOf(
        Obstacle(RectF(600f, floorY - obstacleHeight + 25f, 720f, floorY + 25f), piedra),
        Obstacle(RectF(1000f, floorY - obstacleHeight + 25f, 1120f, floorY + 25f), tronco),
        Obstacle(RectF(1500f, floorY - obstacleHeight + 25f, 1620f, floorY + 25f), puas)
    )

    val goalX = 2000f
    val goalWidth = 320f
    val goalHeight = 350f
    val goalRect = RectF(goalX, floorY - goalHeight + 30f, goalX + goalWidth, floorY + 30f)

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (!levelCompleted) {
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

                cameraX = (playerX - screenW * 0.3f).coerceAtLeast(0f)

                val playerRect = RectF(
                    playerX + 10f,
                    playerY - dogSizeDp.value + 22f,
                    playerX + dogSizeDp.value - 10f,
                    playerY + 22f
                )

                for (obstacle in obstacles) {
                    val o = obstacle.rect
                    if (playerRect.right > o.left && playerRect.left < o.right &&
                        playerRect.bottom > o.top && playerRect.top < o.bottom
                    ) {
                        playerX = o.left - dogSizeDp.value + 10f
                    }
                }

                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right) {
                    levelCompleted = true
                }
            }
        }
    }

    LaunchedEffect(levelCompleted) {
        if (levelCompleted) {
            delay(1500)
            navController.navigate("levels") { popUpTo("level1") { inclusive = true } }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val bgW = bg.width.toFloat()
            for (i in -1..5) drawImage(bg, topLeft = Offset(bgW * i - cameraX, 0f))
        }

        obstacles.forEach {
            Image(bitmap = it.image, contentDescription = null,
                modifier = Modifier
                    .offset((it.rect.left - cameraX).dp, it.rect.top.dp)
                    .size(it.rect.width().dp, it.rect.height().dp))
        }

        Image(bitmap = casaTio, contentDescription = null,
            modifier = Modifier
                .offset((goalRect.left - cameraX).dp, goalRect.top.dp)
                .size(goalWidth.dp, goalHeight.dp))

        Image(bitmap = dog, contentDescription = null,
            modifier = Modifier
                .offset((playerX - cameraX).dp, (playerY - dogSizeDp.value + 22f).dp)
                .size(dogSizeDp))

        // 🔹 Botones
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

