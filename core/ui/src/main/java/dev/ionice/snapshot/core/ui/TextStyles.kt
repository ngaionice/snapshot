package dev.ionice.snapshot.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@OptIn(ExperimentalUnitApi::class)
@Composable
fun titleMediumLarge() = MaterialTheme.typography.titleLarge.copy(fontSize = TextUnit(20F, TextUnitType.Sp), fontWeight = FontWeight.Medium)