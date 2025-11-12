package com.example.guzguzaventuras.ui.levels.bar

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
import kotlin.math.sin

@Composable
fun Level7Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()
    val context = LocalContext.current

    // 🎨 Imágenes
    val fondo = ImageBitmap.imageResource(context.resources, R.drawable.fondo_agua)
    val perro = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val piranaDerecha = ImageBitmap.imageResource(context.resources, R.drawable.pirana_derecha)
    val piranaIzquierda = ImageBitmap.imageResource(context.resources, R.drawable.pirana_izquierda)
    val metaImg = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)

    // 🐶 Jugador
    val dogSize = 100f
    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(screenH / 2) }
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    var cameraX by remember { mutableStateOf(0f) }
    val moveStep = 40f

    // 🐟 Enemigos (pirañas)
    class Fish(
        x: Float, y: Float,
        w: Float = 100f, h: Float = 100f,
        dir: Float, val minX: Float, val maxX: Float, val speed: Float
    ) {
        var x by mutableStateOf(x)
        var y by mutableStateOf(y)
        var w by mutableStateOf(w)
        var h by mutableStateOf(h)
        var dir by mutableStateOf(dir)
        var time by mutableStateOf(0f)
    }

    val fishes = remember {
        mutableStateListOf(
            Fish(x = 600f, y = screenH / 2 - 200f, dir = 1f, minX = 500f, maxX = 900f, speed = 2.5f),
            Fish(x = 1300f, y = screenH / 2, dir = -1f, minX = 1100f, maxX = 1500f, speed = 3.0f),
            Fish(x = 2000f, y = screenH / 2 + 150f, dir = 1f, minX = 1900f, maxX = 2300f, speed = 2.8f),
            Fish(x = 2800f, y = screenH / 2 - 100f, dir = -1f, minX = 2700f, maxX = 3100f, speed = 2.6f),
        )
    }

    // 🏁 Meta
    val goalX = 3500f
    val goalRect = RectF(goalX, screenH / 2 - 150f, goalX + 300f, screenH / 2 + 100f)

    // ================= LOOP PRINCIPAL =================
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (!dead && !completed) {
                playerY = playerY.coerceIn(0f, screenH - dogSize)
                playerX = playerX.coerceAtLeast(0f)
                cameraX = (playerX - screenW * 0.3f).coerceAtLeast(0f)

                val playerRect = RectF(playerX, playerY, playerX + dogSize, playerY + dogSize)
                loop@ for (f in fishes) {
                    val fishRect = RectF(f.x, f.y, f.x + f.w, f.y + f.h)
                    val hit = playerRect.right > fishRect.left &&
                            playerRect.left < fishRect.right &&
                            playerRect.bottom > fishRect.top &&
                            playerRect.top < fishRect.bottom
                    if (hit) {
                        dead = true
                        break@loop
                    }
                }

                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right) {
                    completed = true
                }
            } else if (dead) {
                playerY += 10f
            }
        }
    }

    // ================= LOOP PIRAÑAS =================
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            fishes.forEach { f ->
                f.x += f.dir * f.speed
                f.time += 0.05f
                f.y += sin(f.time) * 0.8f

                if (f.x < f.minX) { f.x = f.minX; f.dir = 1f }
                if (f.x + f.w > f.maxX) { f.x = f.maxX - f.w; f.dir = -1f }
                f.y = f.y.coerceIn(0f, screenH - f.h)
            }
        }
    }

    // ================= EVENTOS =================
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level6") {
                popUpTo("level6") { inclusive = true }
            }
        }
    }

    // ✅ CORRECCIÓN: vuelve al menú del mundo 2
    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels2") { // 🔥 antes decía "levels"
                popUpTo("level6") { inclusive = true }
            }
        }
    }

    // ================= CONTROLES =================
    fun moveLeft() { playerX -= moveStep }
    fun moveRight() { playerX += moveStep }
    fun moveUp() { playerY -= moveStep }
    fun moveDown() { playerY += moveStep }

    // ================= DIBUJO =================
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val scale = size.height / fondo.height.toFloat()
            val scaledWidth = fondo.width * scale
            val repeatCount = (size.width / scaledWidth).toInt() + 2
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                for (i in -1..repeatCount) {
                    val left = i * scaledWidth - cameraX
                    val right = left + scaledWidth
                    val rectDst = Rect(left.toInt(), 0, right.toInt(), size.height.toInt())
                    nativeCanvas.drawBitmap(fondo.asAndroidBitmap(), null, rectDst, null)
                }
            }
        }

        // 🐟 Pirañas
        fishes.forEach { f ->
            val imagen = if (f.dir > 0) piranaDerecha else piranaIzquierda
            Image(
                bitmap = imagen,
                contentDescription = "Piraña enemiga",
                modifier = Modifier
                    .offset((f.x - cameraX).dp, f.y.dp)
                    .size(f.w.dp, f.h.dp)
            )
        }

        // 🏠 Meta
        Image(
            bitmap = metaImg,
            contentDescription = "Meta",
            modifier = Modifier
                .offset((goalRect.left - cameraX).dp, goalRect.top.dp)
                .size(300.dp, 200.dp)
        )

        // 🐶 Jugador
        Image(
            bitmap = perro,
            contentDescription = "Jugador submarino",
            modifier = Modifier
                .offset((playerX - cameraX).dp, playerY.dp)
                .size(dogSize.dp)
        )

        // 🎮 Controles
        Column(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HoldableButton("↑") { moveUp() }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HoldableButton("←") { moveLeft() }
                HoldableButton("↓") { moveDown() }
                HoldableButton("→") { moveRight() }
            }
        }

        // ✅ Mensajes
        if (completed)
            Box(
                Modifier
                    .align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text("¡Nivel completado!", color = Color.White, fontWeight = FontWeight.Bold)
            }

        if (dead)
            Box(
                Modifier
                    .align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text("¡Te comió una piraña!", color = Color.White, fontWeight = FontWeight.Bold)
            }
    }
}