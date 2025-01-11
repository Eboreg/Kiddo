package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.data.types.ListSort

@Composable
fun MediaListSortDialog(
    methods: List<Pair<ListSort.Method, String>>,
    sort: ListSort?,
    onSubmit: (ListSort) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selectedMethod by remember { mutableStateOf<ListSort.Method?>(sort?.method) }
    var descending by remember { mutableStateOf(sort?.order == ListSort.Order.Descending) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(
                    ListSort(
                        method = selectedMethod,
                        order = if (descending) ListSort.Order.Descending else ListSort.Order.Ascending,
                    )
                )
            }) { Text(stringResource(R.string.apply)) }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) } },
        title = { Text(stringResource(R.string.order)) },
        shape = MaterialTheme.shapes.small,
        text = {
            Column {
                for ((method, label) in methods) {
                    MediaListSortRadioButton(
                        label = label,
                        selected = selectedMethod == method,
                        onClick = { selectedMethod = method },
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                    MediaListSortRadioButton(
                        label = stringResource(R.string.ascending),
                        selected = !descending,
                        onClick = { descending = false },
                    )
                    MediaListSortRadioButton(
                        label = stringResource(R.string.descending),
                        selected = descending,
                        onClick = { descending = true },
                    )
                }
            }
        },
    )
}

@Composable
fun MediaListSortRadioButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.height(40.dp).selectable(
            selected = selected,
            onClick = onClick,
            role = Role.RadioButton,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = label)
    }
}
