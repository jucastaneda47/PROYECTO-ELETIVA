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
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun Level8Screen(navController: NavController) {
    val config = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🌊 Imágenes
    val fondo = ImageBitmap.imageResource(context.resources, R.drawable.fondo_agua)
    val perro = ImageBitmap.imageResource(context.resources, R.drawable.intermedio)
    val piranaDerecha = ImageBitmap.imageResource(context.resources, R.drawable.pirana_derecha)
    val piranaIzquierda = ImageBitmap.imageResource(context.resources, R.drawable.pirana_izquierda)
    val piranaDerechaDisparo = ImageBitmap.imageResource(context.resources, R.drawable.pirana_dc_dis)
    val piranaIzquierdaDisparo = ImageBitmap.imageResource(context.resources, R.drawable.pirana_iz_dis)
    val metaImg = ImageBitmap.imageResource(context.resources, R.drawable.casa_tio)
    val flechaDerecha = ImageBitmap.imageResource(context.resources, R.drawable.flecha_derecha)
    val flechaIzquierda = ImageBitmap.imageResource(context.resources, R.drawable.flecha_izquierda)

    // 🐶 Jugador
    val dogSize = 100f
    var playerX by remember { mutableStateOf(100f) }
    var playerY by remember { mutableStateOf(screenH / 2) }
    var dead by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    var cameraX by remember { mutableStateOf(0f) }
    val moveStep = 40f

    // 🐟 Pirañas con animación
    class Pirana(
        val x: Float,
        val y: Float,
        val dir: Int, // 1 = derecha, -1 = izquierda
        val cooldown: Int
    ) {
        var disparando by mutableStateOf(false)
    }

    val piranas = remember {
        mutableStateListOf(
            Pirana(700f, screenH / 2 - 150f, 1, 60),
            Pirana(1500f, screenH / 2, -1, 80),
            Pirana(2300f, screenH / 2 + 100f, 1, 100),
            Pirana(3100f, screenH / 2 - 120f, -1, 60),
            Pirana(3800f, screenH / 2, 1, 90)
        )
    }

    // 🏹 Flechas
    class Flecha(
        var x: Float,
        var y: Float,
        val dir: Int,
        val originX: Float,
        val speed: Float = 8f,
        val maxDistance: Float = 1000f,
        val w: Float = 80f,
        val h: Float = 20f
    )

    val flechas = remember { mutableStateListOf<Flecha>() }

    // 🏁 Meta
    val goalX = 4300f
    val goalRect = RectF(goalX, screenH / 2 - 150f, goalX + 300f, screenH / 2 + 100f)

    // ================= LOOP PRINCIPAL =================
    LaunchedEffect(Unit) {
        var tick = 0
        while (true) {
            delay(16)
            tick++

            if (!dead && !completed) {
                // Movimiento del jugador
                playerY = playerY.coerceIn(0f, screenH - dogSize)
                playerX = playerX.coerceAtLeast(0f)
                cameraX = (playerX - screenW * 0.3f).coerceAtLeast(0f)

                // Disparo de pirañas + animación
                piranas.forEach { p ->
                    if (tick % p.cooldown == 0) {
                        val flechaX = if (p.dir > 0) p.x + 80f else p.x - 80f
                        val flechaY = p.y + 40f
                        flechas.add(Flecha(flechaX, flechaY, p.dir, p.x))

                        // animación de disparo
                        p.disparando = true
                        scope.launch {
                            delay(300)
                            p.disparando = false
                        }
                    }
                }

                // Movimiento de flechas
                val iterator = flechas.iterator()
                while (iterator.hasNext()) {
                    val f = iterator.next()
                    f.x += f.speed * f.dir
                    val distance = abs(f.x - f.originX)
                    if (distance > f.maxDistance) iterator.remove()
                }

                // Colisión con flechas
                val playerRect = RectF(playerX, playerY, playerX + dogSize, playerY + dogSize)
                for (f in flechas) {
                    val flechaRect = RectF(f.x, f.y, f.x + f.w, f.y + f.h)
                    val hit = playerRect.right > flechaRect.left &&
                            playerRect.left < flechaRect.right &&
                            playerRect.bottom > flechaRect.top &&
                            playerRect.top < flechaRect.bottom
                    if (hit) {
                        dead = true
                        break
                    }
                }

                // Meta
                if (playerRect.right > goalRect.left && playerRect.left < goalRect.right)
                    completed = true

            } else if (dead) {
                playerY += 10f
            }
        }
    }

    // ================= REINICIOS =================
    LaunchedEffect(dead) {
        if (dead) {
            delay(1200)
            navController.navigate("level8") { popUpTo("level8") { inclusive = true } }
        }
    }

    // ✅ CORREGIDO → vuelve al menú del segundo mundo
    LaunchedEffect(completed) {
        if (completed) {
            delay(1500)
            navController.navigate("levels2") { // 🔥 antes decía "levels"
                popUpTo("level8") { inclusive = true }
            }
        }
    }

    // ================= CONTROLES =================
    fun moveLeft()  { playerX -= moveStep }
    fun moveRight() { playerX += moveStep }
    fun moveUp()    { playerY -= moveStep }
    fun moveDown()  { playerY += moveStep }

    // ================= DIBUJO =================
    Box(Modifier.fillMaxSize()) {
        // 🌊 Fondo acuático
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

        // 🐟 Pirañas animadas
        piranas.forEach { p ->
            val imagen = when {
                p.dir > 0 && p.disparando -> piranaDerechaDisparo
                p.dir < 0 && p.disparando -> piranaIzquierdaDisparo
                p.dir > 0 -> piranaDerecha
                else -> piranaIzquierda
            }
            Image(
                bitmap = imagen,
                contentDescription = "Piraña fija",
                modifier = Modifier
                    .offset((p.x - cameraX).dp, p.y.dp)
                    .size(100.dp)
            )
        }

        // 🏹 Flechas
        flechas.forEach { f ->
            val img = if (f.dir > 0) flechaDerecha else flechaIzquierda
            Image(
                bitmap = img,
                contentDescription = "Flecha",
                modifier = Modifier
                    .offset((f.x - cameraX - 20f).dp, (f.y - 10f).dp)
                    .size((f.w * 2f).dp, (f.h * 2f).dp)
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
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF3E4A8B), RoundedCornerShape(50))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) { Text("¡Nivel completado!", color = Color.White, fontWeight = FontWeight.Bold) }

        if (dead)
            Box(
                Modifier.align(Alignment.Center)
                    .background(Color(0xFF8B3E3E), RoundedCornerShape(50))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) { Text("¡Una flecha te alcanzó!", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}