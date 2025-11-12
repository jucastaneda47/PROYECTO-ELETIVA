package com.example.guzguzaventuras.ui.levels

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Botón táctil mantenido compatible con todas las versiones de Compose.
 * Reemplaza los anteriores para evitar sobrecarga de funciones.
 */
@Composable
fun HoldableButton(symbol: String, onHold: () -> Unit) {
    var holding by remember { mutableStateOf(false) }

    LaunchedEffect(holding) {
        while (holding) {
            onHold()
            delay(45)
        }
    }

    Box(
        modifier = Modifier
            .size(70.dp)
            .border(2.dp, Color.White)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()      // Espera el primer toque
                        holding = true
                        waitForUpOrCancellation() // Espera que se suelte
                        holding = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, color = Color.White)
    }
}
