package io.simsim.demo.fetal.ui.history

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FetalDemoTheme {
                MarqueeText("HistoryActivity")
            }
        }
    }
}

@Composable
fun MarqueeText(
    text: String,
    repeat: Int = Int.MAX_VALUE,
    orientation: Int = 1 // 1 for horizontal
) {
    MeasureUnconstrainedViewWidth(
        { Text(text = text) }
    ) {
        val textLayoutLength by remember {
            mutableStateOf(it.width.toFloat())
        }
        var initOffset by remember {
            mutableStateOf(0f)
        }
        var targetOffset by remember {
            mutableStateOf(textLayoutLength)
        }
        var offset by remember {
            mutableStateOf(initOffset)
        }
        LaunchedEffect(initOffset, targetOffset) {
            delay(500)
            if (initOffset != targetOffset) {
                val animatable = Animatable(initOffset)
                animatable.animateTo(
                    targetOffset,
                    animationSpec = tween(2000, easing = LinearEasing)
                ) {
                    offset = this.value
                    if (this.value == targetValue) {
                        // come to an end
                        when (targetOffset) {
                            0f -> {
                                initOffset = 0f
                                targetOffset = textLayoutLength
                            }
                            else -> {
                                initOffset = -textLayoutLength
                                targetOffset = 0f
                            }
                        }
                    }
                }
            }
        }
        Text(
            modifier = Modifier
                .offset { IntOffset(x = -offset.roundToInt(), y = 0) },
            overflow = TextOverflow.Clip,
            text = text
        )
    }
}

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (placeable: Placeable) -> Unit
) {
    SubcomposeLayout { constraints ->
        val measuredPlaceable = subcompose("viewToMeasure", viewToMeasure)[0]
            .measure(Constraints())

        val contentPlaceable = subcompose("content") {
            content(measuredPlaceable)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}
