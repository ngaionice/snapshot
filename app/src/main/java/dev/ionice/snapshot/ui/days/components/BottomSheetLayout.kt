package dev.ionice.snapshot.ui.days.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Deprecated("Due to performance issues, content should not be rendered inside this layout. Replace with BottomSheetLayout in BottomSheet.kt.")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetLayout(
    sheetState: ModalBottomSheetState,
    sheetColor: Color? = null,
    sheetContent: @Composable () -> Unit,
    screenContent: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
        sheetBackgroundColor = sheetColor ?: MaterialTheme.colorScheme.background,
        sheetContent = {
            Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        }
    ) {
        screenContent()
    }
}