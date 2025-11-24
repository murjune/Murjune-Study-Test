package com.murjune.pratice.compose.study.sample.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.System.currentTimeMillis

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun CurrentMsText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = remember { TextStyle.Default.copy(fontSize = 20.sp) },
    customMs: Long? = null,
) {
    var msPath: Path? by remember { mutableStateOf(null) }

    Text(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .drawBehind {
                val msPath = msPath ?: return@drawBehind
                drawPath(
                    path = msPath,
                    color = Color.Red,
                    style = Stroke(width = 2.dp.toPx()),
                )
            },
        text = "$text @ ${customMs ?: currentTimeMillis()}",
        style = textStyle,
        textAlign = TextAlign.Center,
        onTextLayout = { layout ->
            msPath = layout.getPathForRange(
                start = layout.layoutInput.text.lastIndexOf('@') + 2,
                end = layout.layoutInput.text.length,
            )
        },
    )
}