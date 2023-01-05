package dev.ionice.snapshot.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquareImageCard(displayText: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.aspectRatio(1f),
        onClick = onClick,
        // TODO: remove this line and use an image instead
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp), contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun SquareImageCardPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.fade(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CardDefaults.shape
            )
    ) {

    }
}

@Preview
@Composable
private fun SquareImageCardPreview() {
    LazyRow(
        modifier = Modifier
            .height(150.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SquareImageCard(displayText = "Short text") {

            }
        }
        item {
            SquareImageCard(displayText = "Very long text that should be cut down") {

            }
        }
        item {
            SquareImageCardPlaceholder()
        }
    }
}
