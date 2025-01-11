package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.clickableIfNotNull

@Composable
fun ItemInfoRow(label: String, text: String, onClick: ((String) -> Unit)? = null) {
    val textStyle =
        if (onClick != null) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
        else LocalTextStyle.current

    ItemInfoRow(label = label) { Text(text, modifier = Modifier.clickableIfNotNull(onClick, text), style = textStyle) }
}

@Composable
fun ItemInfoRow(label: String, texts: Collection<String>, onClick: ((String) -> Unit)? = null) {
    val textStyle =
        if (onClick != null) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
        else LocalTextStyle.current

    ItemInfoRow(label = label) {
        Column(horizontalAlignment = Alignment.End) {
            for (text in texts) {
                Text(
                    text = text,
                    modifier = Modifier.clickableIfNotNull(onClick, text),
                    style = textStyle,
                )
            }
        }
    }
}

@Composable
fun ItemInfoRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            softWrap = false,
            maxLines = 1,
            modifier = Modifier.padding(end = 20.dp)
        )
        content()
    }
}
