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
fun Level8Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()
    val context = LocalContext.current

    // 🌊 Imágenes
    val fondo = ImageBitmap.imageResource(context.resources, R.drawable.fondo_agua)
    val piranaDer = ImageBitmap.imageResource(context.resources, R.drawable.pirana_derecha)
    val piranaIzq = ImageBitmap.imageResource(context.resources, R.drawable.pirana_izquierda)
    val juanQuieto = ImageBitmap.imageResource(context.resources, R.drawable.juantino_agua)
    val juanDer = ImageBitmap.imageResource(context.resources, R.drawable.juantino_agua)
    val juanIzq = ImageBitmap.imageResource(context.resources, R.drawable.juantino_agua)
    val balaJugador = ImageBitmap.imageResource(context.resources, R.drawable.botella)
    val flechaDer = ImageBitmap.imageResource(context.resources, R.drawable.flecha_derecha)
    val flechaIzq = ImageBitmap.imageResource(context.resources, R.drawable.flecha_izquierda)

    // 🐶 Juantino
    var dog by remember { mutableStateOf(juanQuieto) }
    val dogSize = 100f
    var playerX by remember { mutableStateOf(150f) }
    var playerY by remember { mutableStateOf(screenH / 2) }
    var facingRight by remember { mutableStateOf(true) }

    data class Bala(var x: Float, var y: Float, var dir: Float)
    val balas = remember { mutableStateListOf<Bala>() }

    // 👹 Boss (Piraña Gigante)
    var bossX by remember { mutableStateOf(screenW - 300f) }
    var bossY by remember { mutableStateOf(screenH / 2) }
    var bossDirX by remember { mutableStateOf(-1f) }
    var bossDirY by remember { mutableStateOf(0f) }
    var bossSpeed by remember { mutableStateOf(2.5f) }
    var bossVida by remember { mutableStateOf(200) }
    val bossDisparos = remember { mutableStateListOf<Bala>() }

    // ⚙️ Estados
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }

    // ================= BUCLE PRINCIPAL =================
    LaunchedEffect(Unit) {
        var tick = 0
        var cambioDir = 0
        while (true) {
            delay(16)
            tick++

            if (!dead && !completed) {

                // === Movimiento del boss aleatorio ===
                cambioDir++
                if (cambioDir > 90) { // cada ~1.5 segundos cambia dirección aleatoriamente
                    bossDirX = Random.nextFloat() * 2f - 1f // entre -1 y 1
                    bossDirY = Random.nextFloat() * 2f - 1f
                    bossSpeed = Random.nextDouble(1.5, 3.8).toFloat()
                    cambioDir = 0
                }

                bossX += bossDirX * bossSpeed
                bossY += bossDirY * bossSpeed

                // límites de movimiento del boss
                if (bossX < 0) { bossX = 0f; bossDirX = 1f }
                if (bossX > screenW - 200f) { bossX = screenW - 200f; bossDirX = -1f }
                if (bossY < 50f) { bossY = 50f; bossDirY = 1f }
                if (bossY > screenH - 150f) { bossY = screenH - 150f; bossDirY = -1f }

                // Disparos del boss (flechas)
                if (tick % 70 == 0) {
                    val dir = if (bossDirX > 0) 1f else -1f
                    bossDisparos.add(Bala(bossX + 60f, bossY + 50f, dir))
                }

                // Movimiento de disparos del boss
                val removeBossBullets = mutableListOf<Bala>()
                for (b in bossDisparos) {
                    b.x += b.dir * 9f
                    if (b.x < -100f || b.x > screenW + 100f) removeBossBullets.add(b)
                }
                bossDisparos.removeAll(removeBossBullets)

                // Movimiento de balas de Juantino
                val removeBalas = mutableListOf<Bala>()
                for (b in balas) {
                    b.x += b.dir * 14f
                    if (b.x < -100f || b.x > screenW + 100f) removeBalas.add(b)
                }
                balas.removeAll(removeBalas)

                // Colisión bala → boss
                val bossRect = RectF(bossX, bossY, bossX + 180f, bossY + 140f)
                val balaGolpe = balas.firstOrNull {
                    val r = RectF(it.x, it.y, it.x + 40f, it.y + 20f)
                    RectF.intersects(r, bossRect)
                }
                if (balaGolpe != null) {
                    bossVida -= 2
                    balas.remove(balaGolpe)
                    if (bossVida <= 0) completed = true
                }

                // Colisión disparo boss → jugador
                val playerRect = RectF(playerX, playerY, playerX + dogSize, playerY + dogSize)
                for (b in bossDisparos) {
                    val r = RectF(b.x, b.y, b.x + 40f, b.y + 20f)
                    if (RectF.intersects(r, playerRect)) {
                        dead = true
                        break
                    }
                }

                // Colisión directa con el boss
                if (RectF.intersects(bossRect, playerRect)) {
                    dead = true
                }

                // 🧱 Evitar que Juantino salga de la pantalla
                if (playerX < 0f) playerX = 0f
                if (playerX > screenW - dogSize) playerX = screenW - dogSize
                if (playerY < 20f) playerY = 20f
                if (playerY > screenH - dogSize - 50f) playerY = screenH - dogSize - 50f
            }
        }
    }

    // ================= REINICIO =================
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level8") {
                popUpTo("level8") { inclusive = true }
            }
        }
    }

    // ✅ Ahora devuelve al menú del segundo mundo (levels2Screen)
    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels2") { // 🔥 corregido aquí
                popUpTo("level8") { inclusive = true }
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
        val bossImg = if (bossDirX > 0) piranaDer else piranaIzq
        Image(
            bitmap = bossImg,
            contentDescription = "Boss Piraña",
            modifier = Modifier.offset(bossX.dp, bossY.dp).size(180.dp)
        )

        // Barra de vida
        Canvas(Modifier.offset(100.dp, 20.dp).size(250.dp, 20.dp)) {
            drawRect(Color.Gray, size = size)
            val vida = (bossVida / 200f) * size.width
            drawRect(Color.Red, topLeft = Offset.Zero, size = androidx.compose.ui.geometry.Size(vida, size.height))
        }

        // Juantino
        Image(
            bitmap = dog,
            contentDescription = "Jugador",
            modifier = Modifier.offset(playerX.dp, playerY.dp).size(dogSize.dp)
        )

        // Balas de Juantino
        balas.forEach { b ->
            Image(
                bitmap = balaJugador,
                contentDescription = "BalaJugador",
                modifier = Modifier.offset(b.x.dp, b.y.dp).size(40.dp, 40.dp)
            )
        }

        // Balas del boss
        bossDisparos.forEach { b ->
            val img = if (b.dir > 0) flechaDer else flechaIzq
            Image(
                bitmap = img,
                contentDescription = "BalaBoss",
                modifier = Modifier.offset(b.x.dp, b.y.dp).size(45.dp, 25.dp)
            )
        }

        // Controles
        Column(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HoldableButton("↑") { playerY -= 40f; dog = juanQuieto }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HoldableButton("←") { playerX -= 40f; dog = juanIzq; facingRight = false }
                HoldableButton("↓") { playerY += 40f; dog = juanQuieto }
                HoldableButton("→") { playerX += 40f; dog = juanDer; facingRight = true }
            }
            HoldableButton("🔥") {
                val dir = if (facingRight) 1f else -1f
                balas.add(Bala(playerX + dogSize / 2, playerY + 30f, dir))
            }
        }

        // Mensajes
        if (completed)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(16.dp)
            ) {
                Text("¡Derrotaste a la piraña gigante!", color = Color.White, fontWeight = FontWeight.Bold)
            }

        if (dead)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(16.dp)
            ) {
                Text("¡Te devoró la piraña!", color = Color.White, fontWeight = FontWeight.Bold)
            }
    }
}
