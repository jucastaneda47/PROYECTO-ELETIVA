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
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun Level12Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()
    val context = LocalContext.current

    // 🎨 Imágenes
    val fondo = ImageBitmap.imageResource(context.resources, R.drawable.niveles)
    val bossMama = ImageBitmap.imageResource(context.resources, R.drawable.mami)
    val bossPolicia = ImageBitmap.imageResource(context.resources, R.drawable.policia)
    val bossColmenares = ImageBitmap.imageResource(context.resources, R.drawable.diablito)

    val balaMama = ImageBitmap.imageResource(context.resources, R.drawable.chancla)
    val balaPolicia = ImageBitmap.imageResource(context.resources, R.drawable.multa)
    val balaColmenares = ImageBitmap.imageResource(context.resources, R.drawable.tridente)
    val balaJuantino = ImageBitmap.imageResource(context.resources, R.drawable.botella)

    // 🐶 Juantino
    val juanQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val juanDer = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val juanIzq = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val juanSalto = ImageBitmap.imageResource(context.resources, R.drawable.saltar)

    var dog by remember { mutableStateOf(juanQuieto) }
    val dogSize = 100f
    val dogScale = 1.35f
    val floorY = screenH * 0.75f
    var playerX by remember { mutableStateOf(200f) }
    var playerY by remember { mutableStateOf(floorY) }
    var velocityY by remember { mutableStateOf(0f) }
    var jumping by remember { mutableStateOf(false) }
    var facingRight by remember { mutableStateOf(true) }

    data class Bala(var x: Float, var y: Float, var dir: Float)
    val balasJugador = remember { mutableStateListOf<Bala>() }
    val balasBoss = remember { mutableStateListOf<Bala>() }

    // 👹 Boss
    var bossX by remember { mutableStateOf(800f) }
    var bossY by remember { mutableStateOf(floorY - 150f) } // 🔸 posición estándar (mami/policía)
    var bossDir by remember { mutableStateOf(1f) }
    var bossVida by remember { mutableStateOf(70) }
    var phase by remember { mutableStateOf(1) } // 1: mamá, 2: policía, 3: colmenares
    var bossAlive by remember { mutableStateOf(true) }

    // ⚙️ Estados
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }

    val gravity = 2.2f
    val jumpForce = -28f

    // =================== BUCLE PRINCIPAL ===================
    LaunchedEffect(Unit) {
        var tick = 0
        while (true) {
            delay(16)
            tick++

            if (!dead && !completed) {
                // Movimiento Juantino
                if (jumping) {
                    playerY += velocityY
                    velocityY += gravity
                    if (playerY >= floorY) {
                        playerY = floorY
                        velocityY = 0f
                        jumping = false
                        dog = if (facingRight) juanDer else juanIzq
                    }
                }

                // 🔒 Limites pantalla
                if (playerX < 0f) playerX = 0f
                if (playerX > screenW - dogSize) playerX = screenW - dogSize
                if (playerY < 0f) playerY = 0f
                if (playerY > floorY) playerY = floorY

                // Movimiento boss
                val bossSpeed = when (phase) {
                    1 -> 3f
                    2 -> 4f
                    else -> 2.5f
                }
                bossX += bossDir * bossSpeed
                if (bossX < 300f) bossDir = 1f
                if (bossX > screenW - 250f) bossDir = -1f

                // Disparos boss
                val shootRate = when (phase) {
                    1 -> 70
                    2 -> 55
                    else -> 40
                }
                if (tick % shootRate == 0) {
                    val dir = if (bossDir > 0) 1f else -1f
                    val offsetY = if (phase == 3) Random.nextFloat() * 120f - 60f else 0f
                    balasBoss.add(Bala(bossX + 50f, bossY + 50f + offsetY, dir))
                    if (phase == 3) {
                        balasBoss.add(Bala(bossX + 50f, bossY + 30f, dir))
                        balasBoss.add(Bala(bossX + 50f, bossY + 70f, dir))
                    }
                }

                // Movimiento balas
                val removeBoss = mutableListOf<Bala>()
                for (b in balasBoss) {
                    b.x += b.dir * 10f
                    if (phase == 3) b.y += sin(tick / 10f) * 2f
                    if (b.x < -100f || b.x > screenW + 100f) removeBoss.add(b)
                }
                balasBoss.removeAll(removeBoss)

                val removePlayer = mutableListOf<Bala>()
                for (b in balasJugador) {
                    b.x += b.dir * 14f
                    if (b.x < -100f || b.x > screenW + 100f) removePlayer.add(b)
                }
                balasJugador.removeAll(removePlayer)

                // Colisiones
                val bossRect = RectF(
                    bossX,
                    bossY - 70f,
                    bossX + if (phase == 3) 250f else 180f,
                    bossY + if (phase == 3) 200f else 110f
                )

                val balaGolpe = balasJugador.firstOrNull {
                    val r = RectF(it.x, it.y, it.x + 40f, it.y + 20f)
                    RectF.intersects(r, bossRect)
                }
                if (balaGolpe != null) {
                    bossVida -= 2
                    balasJugador.remove(balaGolpe)
                    if (bossVida <= 0) {
                        bossAlive = false
                        balasBoss.clear()
                    }
                }

                val playerRect = RectF(playerX, playerY - dogSize, playerX + dogSize, playerY)
                for (b in balasBoss) {
                    val r = RectF(b.x, b.y, b.x + 40f, b.y + 20f)
                    if (RectF.intersects(r, playerRect)) {
                        dead = true
                        break
                    }
                }

                // Cambios de fase
                if (!bossAlive) {
                    when (phase) {
                        1 -> {
                            phase = 2
                            bossVida = 70
                            bossX = screenW - 400f
                            bossY = floorY - 150f // policía al mismo nivel
                            bossAlive = true
                        }
                        2 -> {
                            phase = 3
                            bossVida = 140
                            bossX = screenW - 400f
                            bossY = floorY - 200f // 🔼 solo el diablito más alto
                            bossAlive = true
                        }
                        3 -> completed = true
                    }
                }
            } else if (dead) playerY += 14f
        }
    }

    // =================== REINICIO ===================
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level12") {
                popUpTo("level12") { inclusive = true }
            }
        }
    }

    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels3") {
                popUpTo("level12") { inclusive = true }
            }
        }
    }

    // =================== DIBUJO ===================
    Box(Modifier.fillMaxSize()) {
        // Fondo
        Canvas(Modifier.fillMaxSize()) {
            val scale = size.height / fondo.height.toFloat()
            val scaledWidth = fondo.width * scale
            val repeatCount = (size.width / scaledWidth).toInt() + 2
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                for (i in -1..repeatCount) {
                    val left = i * scaledWidth
                    val right = left + scaledWidth
                    val rectDst = android.graphics.Rect(left.toInt(), 0, right.toInt(), size.height.toInt())
                    nativeCanvas.drawBitmap(fondo.asAndroidBitmap(), null, rectDst, null)
                }
            }
        }

        // Boss
        val bossImg = when (phase) {
            1 -> bossMama
            2 -> bossPolicia
            else -> bossColmenares
        }
        Image(
            bitmap = bossImg,
            contentDescription = "Boss",
            modifier = Modifier.offset(bossX.dp, (bossY - 70f).dp)
                .size(if (phase == 3) 250.dp else 180.dp)
        )

        // Barra vida
        Canvas(Modifier.offset(100.dp, 20.dp).size(250.dp, 20.dp)) {
            drawRect(Color.Gray, size = size)
            val vida = (bossVida / if (phase == 3) 140f else 70f) * size.width
            drawRect(Color.Red, topLeft = Offset.Zero, size = androidx.compose.ui.geometry.Size(vida, size.height))
        }

        // Juantino
        Image(
            bitmap = dog,
            contentDescription = "Jugador",
            modifier = Modifier.offset(playerX.dp, (playerY - dogSize - 70f).dp)
                .size((dogSize * dogScale).dp)
        )

        // Balas de Juantino
        balasJugador.forEach { b ->
            Image(
                bitmap = balaJuantino,
                contentDescription = "BalaJugador",
                modifier = Modifier.offset(b.x.dp, (b.y - 10f).dp)
                    .size(40.dp, 40.dp)
            )
        }

        // Balas del boss
        val balaImg = when (phase) {
            1 -> balaMama
            2 -> balaPolicia
            else -> balaColmenares
        }
        val balaSize = when (phase) {
            1 -> 80.dp // chancla grande
            2 -> 80.dp // multa grande
            else -> 55.dp
        }
        balasBoss.forEach { b ->
            Image(
                bitmap = balaImg,
                contentDescription = "BalaBoss",
                modifier = Modifier.offset(b.x.dp, b.y.dp).size(balaSize)
            )
        }

        // Controles
        Row(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldableButton("←") { playerX -= 40f; dog = juanIzq; facingRight = false }
            HoldableButton("↑") {
                if (!jumping) {
                    jumping = true; velocityY = jumpForce; dog = juanSalto
                }
            }
            HoldableButton("→") { playerX += 40f; dog = juanDer; facingRight = true }
            HoldableButton("🔥") {
                val dir = if (facingRight) 1f else -1f
                balasJugador.add(Bala(playerX + dogSize / 1.5f, playerY - 110f, dir))
            }
        }

        // Mensajes
        if (completed)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(20.dp)
            ) { Text("¡Has vencido al Gran Colmenares!", color = Color.White, fontWeight = FontWeight.Bold) }

        if (dead)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(20.dp)
            ) { Text("¡Has caído en la batalla final!", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}
