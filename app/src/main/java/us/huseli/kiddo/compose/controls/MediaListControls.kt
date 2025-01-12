package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material.icons.sharp.SortByAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R

@Composable
fun MediaListControls(
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasFilters: Boolean = false,
    showSearch: Boolean = false,
    freetext: String = "",
    onSearch: (String?) -> Unit,
    focusRequester: FocusRequester,
) {
    var isSearchVisible by rememberSaveable { mutableStateOf(showSearch) }
    var freetext by rememberSaveable { mutableStateOf(freetext) }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
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
                ToggleChip(
                    value = isSearchVisible,
                    onClick = { isSearchVisible = !isSearchVisible },
                    label = { Text(stringResource(R.string.search)) },
                    leadingIcon = { Icon(Icons.Sharp.Search, null) },
                )
            }
            if (isSearchVisible) {
                SideEffect { focusRequester.requestFocus() }

                OutlinedTextField(
                    value = freetext,
                    onValueChange = { freetext = it },
                    singleLine = true,
                    trailingIcon = { IconButton(onClick = { onSearch(freetext) }) { Icon(Icons.Sharp.Search, null) } },
                    leadingIcon = { IconButton(onClick = { onSearch(null) }) { Icon(Icons.Sharp.Clear, null) } },
                    keyboardActions = KeyboardActions { onSearch(freetext) },
                    modifier = Modifier.focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                )
            }
        }
    }
}
