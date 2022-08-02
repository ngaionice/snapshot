package me.ionice.snapshot.ui.days.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun ListScreenBase(
    modifier: Modifier = Modifier,
    topBarHeightProvider: () -> Dp,
    scrollableTopBar: @Composable (heightOffset: IntOffset) -> Unit,
    content: @Composable (contentPadding: PaddingValues) -> Unit
) {
    val topBarHeightPx = with(LocalDensity.current) { topBarHeightProvider().roundToPx().toFloat() }
    val topBarOffsetHeightPx = remember { mutableStateOf(0f) }

    val contentPadding = PaddingValues(top = topBarHeightProvider())
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = topBarOffsetHeightPx.value + delta
                topBarOffsetHeightPx.value = newOffset.coerceIn(-topBarHeightPx, 0f)

                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        content(contentPadding)
        scrollableTopBar(IntOffset(x = 0, y = topBarOffsetHeightPx.value.roundToInt()))
    }
}