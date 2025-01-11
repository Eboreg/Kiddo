package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.material.icons.sharp.SortByAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R

@Composable
fun MediaListControls(
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasFilters: Boolean = false,
) {
    Surface {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.padding(5.dp).fillMaxWidth()
        ) {
            ToggleChip(
                value = hasFilters,
                onClick = onFilterClick,
                label = { Text(stringResource(R.string.filters)) },
                leadingIcon = { Icon(Icons.Sharp.FilterList, null) },
            )
            ToggleChip(
                value = true,
                onClick = onSortClick,
                label = { Text(stringResource(R.string.sort)) },
                leadingIcon = { Icon(Icons.Sharp.SortByAlpha, null) },
            )
        }
    }
}
