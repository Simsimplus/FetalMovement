package io.simsim.demo.fetal.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.sp

@Composable
fun CenterAlignRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun CenterAlignColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

val ctx: Context
    @Composable get() = LocalContext.current

val cs
    @Composable get() = rememberCoroutineScope()

fun Modifier.silentClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = clickable(
    indication = null,
    interactionSource = MutableInteractionSource(),
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    onClick = onClick
)

@Composable
fun ProvideContentColor(contentColor: Color, content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalContentColor.provides(contentColor), content = content)

@Composable
fun Modifier.drawFadingWord() = composed {
    val textColor = MaterialTheme.colorScheme.primary.toArgb()
    val textSize = with(LocalDensity.current) {
        16.sp.toPx()
    }
    drawWithContent {
        drawImage(
            textAsBitmap("hello", textSize, textColor).asImageBitmap(),
            alpha = 0.1f
        )
    }
}

fun textAsBitmap(text: String, textSize: Float, textColor: Int): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = textSize
    paint.color = textColor
    paint.textAlign = Paint.Align.LEFT
    val baseline: Float = -paint.ascent() // ascent() is negative
    val width = (paint.measureText(text) + 0.5f).toInt() // round
    val height = (baseline + paint.descent() + 0.5f).toInt()
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawText(text, 0f, baseline, paint)
    return image
}
