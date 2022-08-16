package io.simsim.demo.fetal.ui.widgt

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun RocketWord(
    word: String,
    position: DpOffset
) {
    val ani = remember {
        Animatable(0f)
    }
    LaunchedEffect(position) {
        ani.snapTo(0f)
        ani.animateTo(100f, animationSpec = tween(durationMillis = 500, easing = LinearEasing))
    }
    Text(
        modifier = Modifier
            .offset(x = position.x, y = position.y - (ani.value * 0.88f).dp),
        text = word,
        textAlign = TextAlign.Center,
        color = LocalContentColor.current.copy(alpha = 1f - (ani.value / 100f))
    )
}
