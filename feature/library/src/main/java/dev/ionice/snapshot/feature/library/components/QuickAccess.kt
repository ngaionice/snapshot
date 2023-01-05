package dev.ionice.snapshot.feature.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.ui.components.CardButton
import dev.ionice.snapshot.core.ui.components.PageSectionContent
import dev.ionice.snapshot.feature.library.R

@Composable
internal fun QuickAccess(onSelectFavorites: () -> Unit, onSelectRandom: () -> Unit) {
    PageSectionContent {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardButton(
                label = stringResource(R.string.qa_label_favorites),
                onClick = onSelectFavorites,
                modifier = Modifier
                    .weight(1f)
                    .testTag(stringResource(R.string.tt_qa_favorites)),
                leadingIcon = Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(R.string.cd_qa_label_favorites),
            )

            CardButton(
                label = stringResource(R.string.qa_label_random),
                onClick = onSelectRandom,
                modifier = Modifier
                    .weight(1f)
                    .testTag(stringResource(R.string.tt_qa_random)),
                enabled = false,
                leadingIcon = Icons.Outlined.Casino,
                contentDescription = stringResource(R.string.cd_qa_label_random),
            )
        }
    }
}