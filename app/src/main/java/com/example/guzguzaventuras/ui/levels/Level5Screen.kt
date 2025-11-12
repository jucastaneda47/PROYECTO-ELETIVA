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
fun Level5Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp
    val screenW = config.screenWidthDp
    val context = LocalContext.current

    // Imágenes
    val bg = ImageBitmap.imageResource(context.resources, R.drawable.niveles)
    val piedra = ImageBitmap.imageResource(context.resources, R.drawable.piedra)
    val tronco = ImageBitmap.imageResource(context.resources, R.drawable.tronco)
    val casaTio = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)
    val policia = ImageBitmap.imageResource(context.resources, R.drawable.policia)

    // Personaje
    val dogQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val dogRight = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val dogLeft = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val dogJump = ImageBitmap.imageResource(context.resources, R.drawable.saltar)
    var dog by remember { mutableStateOf(dogQuieto) }

    val dogSizeDp = 100.dp
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

    // Obstáculos (piedra, tronco, etc.) alineados al piso
    val H = 100f
    val obstacles = listOf(
        RectF( 900f, floorY - H, 1020f, floorY),
        RectF(1400f, floorY - H, 1520f, floorY),
        RectF(2600f, floorY - H, 2720f, floorY),
        RectF(3000f, floorY - H, 3120f, floorY),
        RectF(3400f, floorY - H, 3520f, floorY)
    )

    // Policías móviles — cada X es estado observable (¡esto fuerza recomposición!)
    val enemyY = floorY - 100f
    val enemyW = 100f
    val enemyH = 100f

    // posiciones
    val enemyX = remember { mutableStateListOf(500f, 1900f, 2250f, 3800f, 4700f) }
    // direcciones
    val enemyDir = remember { mutableStateListOf(1f, 1f, -1f, 1f, 1f) }
    // rangos de patrulla (pares start-end)
    val start = floatArrayOf(500f, 1900f, 2250f, 3800f, 4700f)
    val end   = floatArrayOf(800f, 2200f, 2500f, 4100f, 4950f)

    // Meta
    val goalX = 5200f
    val goalRect = RectF(goalX, floorY - 350f + 30f, goalX + 320f, floorY + 30f)

    // Bucle: física + movimiento continuo de enemigos (independiente del jugador)
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)

            if (!dead && !completed) {
                // salto/gravedad
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

                // mover policías y escribir en State (esto SIEMPRE redibuja)
                for (i in enemyX.indices) {
                    val next = enemyX[i] + enemyDir[i] * 3f
                    enemyX[i] = next
                    if (next < start[i]) enemyDir[i] = 1f
                    if (next > end[i])   enemyDir[i] = -1f
                }

                // cámara
                cameraX = (playerX - screenW * 0.3f).coerceAtLeast(0f)

                // hitbox jugador
                val playerRect = RectF(
                    playerX + 15f,
                    playerY - dogSizeDp.value + 25f,
                    playerX + dogSizeDp.value - 15f,
                    playerY + 25f
                )

                // colisiones con obstáculos
                for (o in obstacles) {
                    val overlap = playerRect.bottom > o.top && playerRect.top < o.bottom &&
                            playerRect.right > o.left && playerRect.left < o.right
                    if (overlap) {
                        if (playerRect.right > o.left && playerRect.left < o.left)
                            playerX = o.left - dogSizeDp.value + 5f
                        else if (playerRect.left < o.right && playerRect.right > o.right)
                            playerX = o.right + 5f
                    }
                }

                // colisiones con policías (matan)
                for (i in enemyX.indices) {
                    val er = RectF(enemyX[i], enemyY, enemyX[i] + enemyW, enemyY + enemyH)
                    val collide = playerRect.right > er.left && playerRect.left < er.right &&
                            playerRect.bottom > er.top && playerRect.top < er.bottom
                    if (collide) { dead = true; break }
                }

                // meta
                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right)
                    completed = true
            } else if (dead) {
                playerY += 14f
            }
        }
    }

    // reinicio / vuelta a niveles
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level5") { popUpTo("level5") { inclusive = true } }
        }
    }
    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels") { popUpTo("level5") { inclusive = true } }
        }
    }

    // Dibujo
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val bgW = bg.width.toFloat()
            for (i in -1..12) drawImage(bg, topLeft = Offset(bgW * i - cameraX, 0f))
        }

        obstacles.forEachIndexed { i, r ->
            val img = if (i % 2 == 0) piedra else tronco
            Image(img, null,
                Modifier.offset((r.left - cameraX).dp, r.top.dp).size(r.width().dp, r.height().dp)
            )
        }

        enemyX.forEach { ex ->
            Image(
                bitmap = policia, contentDescription = "Policía",
                modifier = Modifier.offset((ex - cameraX).dp, enemyY.dp).size(enemyW.dp, enemyH.dp)
            )
        }

        Image(
            bitmap = casaTio, contentDescription = "Meta",
            modifier = Modifier.offset((goalRect.left - cameraX).dp, goalRect.top.dp)
                .size(320.dp, 350.dp)
        )

        Image(
            bitmap = dog, contentDescription = null,
            modifier = Modifier.offset((playerX - cameraX).dp, (playerY - dogSizeDp.value + 20f).dp)
                .size(dogSizeDp)
        )

        Row(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldableButton("←") { playerX -= 40f; if (!jumping) dog = dogLeft }
            HoldableButton("↑") { if (!jumping) { jumping = true; velocity = jumpForce; dog = dogJump } }
            HoldableButton("→") { playerX += 40f; if (!jumping) dog = dogRight }
        }

        if (completed)
            Box(Modifier.align(Alignment.Center).background(Color(0xFF3E4A8B), RoundedCornerShape(50)).padding(16.dp)) {
                Text("¡Nivel completado!", color = Color.White, fontWeight = FontWeight.Bold)
            }
        if (dead)
            Box(Modifier.align(Alignment.Center).background(Color(0xFF8B3E3E), RoundedCornerShape(50)).padding(16.dp)) {
                Text("¡El policía te atrapó!", color = Color.White, fontWeight = FontWeight.Bold)
            }
    }
}
