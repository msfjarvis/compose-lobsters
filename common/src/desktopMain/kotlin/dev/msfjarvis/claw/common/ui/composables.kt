package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.lazyImageResource

@Composable
actual fun NetworkImage(
    url: String,
    contentDescription: String,
    modifier: Modifier,
) {
    KamelImage(
        resource = lazyImageResource(url),
        contentDescription = contentDescription,
        modifier = Modifier.then(modifier),
        crossfade = true,
    )
}
