package com.example.guzguzaventuras.ui.levels.club

import android.graphics.Rect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guzguzaventuras.R
import com.example.guzguzaventuras.ui.levels.HoldableButton
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Level9Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()
    val context = LocalContext.current

    // 🌃 Imágenes
    val fondo = ImageBitmap.imageResource(context.resources, R.drawable.fondo_noche)
    val perroQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val perroDerecha = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val perroIzquierda = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val perroSalto = ImageBitmap.imageResource(context.resources, R.drawable.saltar)
    val paloma = ImageBitmap.imageResource(context.resources, R.drawable.paloma)
    val basura = ImageBitmap.imageResource(context.resources, R.drawable.basura)
    val hindigente = ImageBitmap.imageResource(context.resources, R.drawable.hindigente)
    val policia = ImageBitmap.imageResource(context.resources, R.drawable.policia)
    val flechaImg = ImageBitmap.imageResource(context.resources, R.drawable.pipe)
    val casaTio = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)

    // 🐶 Jugador
    var dog by remember { mutableStateOf(perroQuieto) }
    val dogSize = 100f
    val floorY = screenH * 0.82f
    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(floorY) }
    var velocityY by remember { mutableStateOf(0f) }
    var jumping by remember { mutableStateOf(false) }
    var cameraX by remember { mutableStateOf(0f) }
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }

    val gravity = 2.2f
    val jumpForce = -28f

    // ================= PALOMAS =================
    class Paloma(val x: Float, val y: Float, val cooldown: Int)
    val palomas = remember {
        mutableStateListOf(
            Paloma(1000f, 10f, 90),
            Paloma(2300f, 15f, 85),
            Paloma(3200f, 5f, 95),
            Paloma(4200f, 20f, 90),
            Paloma(5400f, 15f, 90),
            Paloma(6500f, 10f, 100)
        )
    }

    // ================= POLICÍAS =================
    class PoliciaMovil(
        var x: Float,
        var dir: Float,
        val minX: Float,
        val maxX: Float,
        var speed: Float
    )
    val policias = remember {
        mutableStateListOf(
            PoliciaMovil(4400f, 1f, 4400f, 4700f, 2.5f)
        )
    }

    // ================= OBSTÁCULOS =================
    val H = 100f * 1.3f
    val obstacles = listOf(
        RectF(1400f, floorY - H + 20f, 1520f, floorY + 20f),
        RectF(1900f, floorY - H + 20f, 2020f, floorY + 20f),
        RectF(2500f, floorY - H + 20f, 2620f, floorY + 20f),
        RectF(3500f, floorY - H + 20f, 3620f, floorY + 20f),
        RectF(5100f, floorY - H + 20f, 5220f, floorY + 20f),
        RectF(5900f, floorY - H + 20f, 6020f, floorY + 20f)
    )

    // ================= FLECHAS =================
    class Flecha(
        var x: Float,
        var y: Float,
        var speedY: Float = 0f,
        val gravity: Float = 1.8f,
        val w: Float = 40f,
        val h: Float = 100f
    )
    val flechas = remember { mutableStateListOf<Flecha>() }

    // ================= META =================
    val goalX = 7000f
    val goalRect = RectF(goalX, floorY - 350f + 30f, goalX + 320f, floorY + 30f)

    // ================= BUCLE PRINCIPAL =================
    LaunchedEffect(Unit) {
        var tick = 0
        while (true) {
            delay(16)
            tick++

            if (!dead && !completed) {
                // Salto y gravedad
                if (jumping) {
                    playerY += velocityY
                    velocityY += gravity
                    if (playerY >= floorY) {
                        playerY = floorY
                        velocityY = 0f
                        jumping = false
                        dog = perroQuieto
                    }
                }

                // Cámara
                cameraX = (playerX - screenW * 0.3f).coerceAtLeast(0f)

                // Movimiento policías
                policias.forEach { p ->
                    p.x += p.dir * p.speed
                    if (p.x < p.minX || p.x > p.maxX) p.dir *= -1
                    if (tick % 60 == 0) {
                        p.speed = Random.nextFloat() * 4f + 1.5f
                    }
                }

                // Disparos palomas
                palomas.forEach { p ->
                    if (tick % p.cooldown == 0) {
                        flechas.add(Flecha(p.x + 40f, p.y + 60f))
                    }
                }

                // Movimiento flechas
                val iterator = flechas.iterator()
                while (iterator.hasNext()) {
                    val f = iterator.next()
                    f.speedY += f.gravity
                    f.y += f.speedY
                    if (f.y > screenH + 200f) iterator.remove()
                }

                // Colisiones
                val playerRect = RectF(
                    playerX + 15f,
                    playerY - dogSize + 25f,
                    playerX + dogSize - 15f,
                    playerY + 25f
                )

                // Obstáculos
                obstacles.forEachIndexed { _, o ->
                    val overlap = playerRect.bottom > o.top && playerRect.top < o.bottom &&
                            playerRect.right > o.left && playerRect.left < o.right
                    if (overlap) {
                        if (playerRect.right > o.left && playerRect.left < o.left)
                            playerX = o.left - dogSize + 5f
                        else if (playerRect.left < o.right && playerRect.right > o.right)
                            playerX = o.right + 5f
                    }
                }

                // Policías
                for (p in policias) {
                    val rectPol = RectF(p.x, floorY - 100f, p.x + 130f, floorY)
                    val collide = playerRect.right > rectPol.left &&
                            playerRect.left < rectPol.right &&
                            playerRect.bottom > rectPol.top &&
                            playerRect.top < rectPol.bottom
                    if (collide) dead = true
                }

                // Flechas
                for (f in flechas) {
                    val rectFlecha = RectF(f.x, f.y, f.x + f.w, f.y + f.h)
                    val collide = playerRect.right > rectFlecha.left &&
                            playerRect.left < rectFlecha.right &&
                            playerRect.bottom > rectFlecha.top &&
                            playerRect.top < rectFlecha.bottom
                    if (collide) dead = true
                }

                // Meta
                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right)
                    completed = true

            } else if (dead) {
                playerY += 14f
            }
        }
    }

    // ================= REINICIOS =================
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level9") { popUpTo("level9") { inclusive = true } }
        }
    }

    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            // ✅ Ahora vuelve al menú del Mundo 3
            navController.navigate("levels3") { popUpTo("level9") { inclusive = true } }
        }
    }

    // ================= DIBUJO =================
    Box(Modifier.fillMaxSize()) {
        // Fondo
        Canvas(Modifier.fillMaxSize()) {
            val scale = size.height / fondo.height.toFloat()
            val scaledWidth = fondo.width * scale
            val repeatCount = (7000 / scaledWidth).toInt() + 4
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                for (i in -1..repeatCount) {
                    val left = i * scaledWidth - cameraX
                    val rectDst = Rect(
                        left.toInt(), 0, (left + scaledWidth).toInt(), size.height.toInt()
                    )
                    nativeCanvas.drawBitmap(fondo.asAndroidBitmap(), null, rectDst, null)
                }
            }
        }

        // Obstáculos
        obstacles.forEachIndexed { i, r ->
            val img = if (i % 2 == 0) basura else hindigente
            Image(
                bitmap = img,
                contentDescription = "Obstáculo",
                modifier = Modifier
                    .offset((r.left - cameraX).dp, (r.top + 20f).dp)
                    .size((r.width() * 1.3f).dp, (r.height() * 1.3f).dp)
            )
        }

        // Policía — Subido al nivel de Juantino
        policias.forEach { p ->
            Image(
                bitmap = policia,
                contentDescription = "Policía",
                modifier = Modifier
                    .offset((p.x - cameraX).dp, (floorY - 100f).dp)
                    .size(130.dp)
            )
        }

        // Palomas
        palomas.forEach { p ->
            Image(
                bitmap = paloma,
                contentDescription = "Paloma enemiga",
                modifier = Modifier
                    .offset((p.x - cameraX).dp, p.y.dp)
                    .size(70.dp)
            )
        }

        // Flechas
        flechas.forEach { f ->
            Image(
                bitmap = flechaImg,
                contentDescription = "Flecha",
                modifier = Modifier
                    .offset((f.x - cameraX).dp, f.y.dp)
                    .size(f.w.dp, f.h.dp)
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

        // Jugador
        Image(
            bitmap = dog,
            contentDescription = "Jugador",
            modifier = Modifier
                .offset((playerX - cameraX).dp, (playerY - dogSize + 20f).dp)
                .size(dogSize.dp)
        )

        // Controles
        Row(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldableButton("←") { playerX -= 40f; if (!jumping) dog = perroIzquierda }
            HoldableButton("↑") {
                if (!jumping) {
                    jumping = true; velocityY = jumpForce; dog = perroSalto
                }
            }
            HoldableButton("→") { playerX += 40f; if (!jumping) dog = perroDerecha }
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
            ) { Text("¡Te eliminaron!", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}