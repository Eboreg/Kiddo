package us.huseli.kiddo.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import us.huseli.retaintheme.extensions.takeIfNotEmpty

@Composable
fun MediaListItem(
    title: String,
    index: Int,
    placeholderIcon: ImageVector,
    subTitle: String?,
    genres: Collection<String>?,
    supportingContent: String?,
    rating: Double?,
    thumbnail: ImageBitmap?,
    thumbnailSize: DpSize,
    isCurrentItem: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        tonalElevation = 10.dp * ((index + 1) % 2),
        headlineContent = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                subTitle?.let {
                    Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    genres?.takeIfNotEmpty()?.forEach { genre ->
                        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                            Text(genre)
                        }
                    }
                }
            }
        },
        overlineContent = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    supportingContent?.let {
                        Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                rating?.takeIf { it > 0.0 }?.let { RatingStars(it, starSize = 12.dp) }
            }
        },
        leadingContent = {
            Surface(
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.size(thumbnailSize),
            ) {
                thumbnail?.also {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run { Icon(placeholderIcon, null, modifier = Modifier.fillMaxSize()) }
            }
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .then(
                if (isCurrentItem) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                ) else Modifier
            ),
    )
}
