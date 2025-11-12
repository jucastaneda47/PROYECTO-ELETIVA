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
fun Level4Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()
    val context = LocalContext.current

    // 🎨 Imágenes
    val fondo = ImageBitmap.imageResource(context.resources, R.drawable.niveles)
    val bossDer = ImageBitmap.imageResource(context.resources, R.drawable.mami)
    val bossIzq = ImageBitmap.imageResource(context.resources, R.drawable.mami)
    val juanQuieto = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val juanDer = ImageBitmap.imageResource(context.resources, R.drawable.hacia_delante)
    val juanIzq = ImageBitmap.imageResource(context.resources, R.drawable.hacia_atras)
    val juanSalto = ImageBitmap.imageResource(context.resources, R.drawable.saltar)

    // 🧨 Nuevos proyectiles separados
    val balaJuantino = ImageBitmap.imageResource(context.resources, R.drawable.botella)
    val balaMama = ImageBitmap.imageResource(context.resources, R.drawable.chancla)

    // 🐶 Juantinofxd
    var dog by remember { mutableStateOf(juanQuieto) }
    val dogSize = 100f
    val dogScale = 1.35f
    val floorY = screenH * 0.75f
    var playerX by remember { mutableStateOf(200f) }
    var playerY by remember { mutableStateOf(floorY) }
    var velocityY by remember { mutableStateOf(0f) }
    var jumping by remember { mutableStateOf(false) }
    var facingRight by remember { mutableStateOf(true) }

    // 💥 Balas de Juantino
    data class Bala(var x: Float, var y: Float, var dir: Float)
    val balas = remember { mutableStateListOf<Bala>() }

    // 👹 Boss (mamá)
    var bossX by remember { mutableStateOf(800f) }
    var bossY by remember { mutableStateOf(floorY - 150f) }
    var bossDir by remember { mutableStateOf(1f) }
    var bossVida by remember { mutableStateOf(100) }
    val bossDisparos = remember { mutableStateListOf<Bala>() }

    // ⚙️ Estados
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }

    val gravity = 2.2f
    val jumpForce = -28f

    // ================= BUCLE PRINCIPAL =================
    LaunchedEffect(Unit) {
        var tick = 0
        while (true) {
            delay(16)
            tick++

            if (!dead && !completed) {

                // Movimiento y salto del jugador
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

                // Movimiento del boss
                bossX += bossDir * 3f
                if (bossX < 400f) bossDir = 1f
                if (bossX > screenW - 250f) bossDir = -1f

                // Disparos del boss aleatorios
                if (tick % 60 == 0) {
                    val tipo = Random.nextInt(0, 3)
                    when (tipo) {
                        0 -> { // horizontal
                            val dir = if (bossDir > 0) 1f else -1f
                            bossDisparos.add(Bala(bossX, bossY - 10f, dir))
                        }
                        1 -> { // diagonal
                            val dir = if (bossDir > 0) 1f else -1f
                            bossDisparos.add(Bala(bossX, bossY - 20f, dir))
                        }
                        2 -> { // hacia arriba
                            bossDisparos.add(Bala(bossX + 50f, bossY - 30f, 0f))
                        }
                    }
                }

                // Movimiento de las balas del boss (chanclas)
                val toRemoveBoss = mutableListOf<Bala>()
                for (b in bossDisparos) {
                    if (b.dir == 0f) {
                        b.y -= 8f
                    } else {
                        b.x += b.dir * 10f
                        b.y += sin(tick / 10f) * 3f
                    }
                    if (b.x < -100f || b.x > screenW + 100f || b.y < -100f) {
                        toRemoveBoss.add(b)
                    }
                }
                bossDisparos.removeAll(toRemoveBoss)

                // Movimiento de las balas del jugador (botellas)
                val toRemoveBalas = mutableListOf<Bala>()
                for (b in balas) {
                    b.x += b.dir * 14f
                    if (b.x < -100f || b.x > screenW + 100f) {
                        toRemoveBalas.add(b)
                    }
                }
                balas.removeAll(toRemoveBalas)

                // Colisión bala (botella) → boss
                val bossRect = RectF(bossX, bossY - 70f, bossX + 180f, bossY + 110f)
                val balaGolpe = balas.firstOrNull {
                    val r = RectF(it.x, it.y, it.x + 40f, it.y + 20f)
                    RectF.intersects(r, bossRect)
                }
                if (balaGolpe != null) {
                    bossVida -= 2
                    balas.remove(balaGolpe)
                    if (bossVida <= 0) completed = true
                }

                // Colisión disparo del boss (chancla) → jugador
                val playerRect = RectF(playerX, playerY - dogSize, playerX + dogSize, playerY)
                for (b in bossDisparos) {
                    val r = RectF(b.x, b.y, b.x + 40f, b.y + 20f)
                    if (RectF.intersects(r, playerRect)) {
                        dead = true
                        break
                    }
                }
            } else if (dead) {
                playerY += 14f
            }
        }
    }

    // ================= REINICIO =================
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level4") {
                popUpTo("level4") { inclusive = true }
            }
        }
    }
    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels") {
                popUpTo("level4") { inclusive = true }
            }
        }
    }

    // ================= DIBUJO =================
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
        val bossImage = if (bossDir > 0) bossDer else bossIzq
        Image(
            bitmap = bossImage,
            contentDescription = "Boss",
            modifier = Modifier.offset(bossX.dp, (bossY - 70f).dp).size(180.dp)
        )

        // Barra de vida del boss
        Canvas(Modifier.offset(100.dp, 20.dp).size(200.dp, 20.dp)) {
            drawRect(Color.Gray, size = size)
            val vida = (bossVida / 100f) * size.width
            drawRect(Color.Red, topLeft = Offset.Zero, size = androidx.compose.ui.geometry.Size(vida, size.height))
        }

        // 🐶 Juantino
        Image(
            bitmap = dog,
            contentDescription = "Jugador",
            modifier = Modifier
                .offset(playerX.dp, (playerY - dogSize - 70f).dp)
                .size((dogSize * dogScale).dp)
        )

        // 💣 Balas de Juantino (botellas)
        balas.forEach { b ->
            Image(
                bitmap = balaJuantino,
                contentDescription = "BalaJugadorBotella",
                modifier = Modifier.offset(b.x.dp, (b.y - 10f).dp).size(40.dp, 40.dp)
            )
        }

        // 🥿 Balas del boss (chanclas)
        bossDisparos.forEach { b ->
            Image(
                bitmap = balaMama,
                contentDescription = "BalaBossChancla",
                modifier = Modifier.offset(b.x.dp, (b.y - 15f).dp).size(50.dp, 35.dp)
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
                    jumping = true
                    velocityY = jumpForce
                    dog = juanSalto
                }
            }
            HoldableButton("→") { playerX += 40f; dog = juanDer; facingRight = true }
            HoldableButton("🔥") {
                val dir = if (facingRight) 1f else -1f
                // 🎯 Ajuste del punto de salida (hocico)
                balas.add(Bala(playerX + dogSize / 1.5f, playerY - 110f, dir))
            }
        }

        // Mensajes
        if (completed)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(16.dp)
            ) {
                Text("¡Derrotaste a mamá!", color = Color.White, fontWeight = FontWeight.Bold)
            }

        if (dead)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(16.dp)
            ) {
                Text("¡Has sido castigado!", color = Color.White, fontWeight = FontWeight.Bold)
            }
    }
}
