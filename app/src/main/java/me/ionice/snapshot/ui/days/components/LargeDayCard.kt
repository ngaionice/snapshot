//package me.ionice.snapshot.ui.days.components
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.pluralStringResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import me.ionice.snapshot.R
//import me.ionice.snapshot.data.database.v1.day.DayWithMetrics
//import me.ionice.snapshot.ui.common.components.VerticalDivider
//import me.ionice.snapshot.utils.FakeData
//import me.ionice.snapshot.utils.Utils
//import me.ionice.snapshot.utils.Utils.locale
//import java.time.LocalDate
//import java.time.format.TextStyle
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LargeDayCard(
//    day: DayWithMetrics,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    cardInformation: @Composable (Color) -> Unit
//) {
//    val containerColor = MaterialTheme.colorScheme.primaryContainer
//    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//    Card(
//        modifier = modifier,
//        colors = CardDefaults.cardColors(containerColor = containerColor),
//        onClick = onClick
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
//        ) {
//            Text(text = day.core.summary, color = contentColor)
//
//            Divider(color = contentColor)
//
//            cardInformation(contentColor)
//        }
//    }
//}
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun LargeDayCardInformation(
//    textColor: Color,
//    relativeDate: String? = null,
//    date: LocalDate,
//    location: String? = null,
//    metricCount: Int? = null
//) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (relativeDate != null) {
//            Text(
//                text = relativeDate,
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//        } else {
//            Text(
//                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//            VerticalDivider(color = textColor)
//            Text(
//                text = date.format(Utils.shortDateFormatter),
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//        }
//
//        if (location != null) {
//            VerticalDivider(color = textColor)
//            Text(
//                text = location,
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//        }
//        if (metricCount != null) {
//            VerticalDivider(color = textColor)
//            Text(
//                text = pluralStringResource(
//                    R.plurals.day_screen_metric_count,
//                    metricCount,
//                    metricCount
//                ),
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun LargeDayCardPreview() {
//    val day = FakeData.longSummaryEntry
//    LargeDayCard(day = FakeData.longSummaryEntry, onClick = {}) {
//        LargeDayCardInformation(
//            date = LocalDate.ofEpochDay(day.core.id),
//            textColor = it,
//            metricCount = day.metrics.size
//        )
//    }
//}