package dev.ionice.snapshot.feature.search.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetLayout(
    sheetState: ModalBottomSheetState,
    focusRequester: FocusRequester,
    sheetContent: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable(),
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        }
    ) {}
}

enum class BottomSheetContentType {
    DATE,
    LOCATION
}