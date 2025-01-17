package us.huseli.kiddo.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.compose.controls.NiceLinearProgressIndicator
import us.huseli.retaintheme.extensions.takeIfNotEmpty

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaListItem(
    title: String,
    index: Int,
    titleMaxLines: Int = 1,
    placeholderIcon: ImageVector? = null,
    thumbnailSize: DpSize = DpSize(width = 100.dp, height = Dp.Unspecified),
    thumbnailPadding: PaddingValues = PaddingValues(),
    isCurrentItem: Boolean = false,
    onClick: (() -> Unit)? = null,
    subTitle: @Composable ColumnScope.() -> Unit = {},
    genres: Collection<String>? = null,
    supportingContent: String? = null,
    rating: Double? = null,
    thumbnail: ImageBitmap? = null,
    progress: Float? = null,
    baseTonalElevation: Dp = 0.dp,
    alternateTonalElevationIncrement: Dp = 10.dp,
    modifier: Modifier = Modifier,
) {
    val tonalElevation =
        if ((index + 1) % 2 == 0) baseTonalElevation
        else baseTonalElevation + alternateTonalElevationIncrement

    ListItem(
        tonalElevation = tonalElevation,
        headlineContent = {
            Text(
                text = title,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                subTitle()
                genres?.takeIfNotEmpty()?.also { genres ->
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        maxLines = 1,
                        overflow = FlowRowOverflow.Clip,
                    ) {
                        genres.forEach { genre ->
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                content = { Text(genre) },
                            )
                        }
                    }
                }
            }
        },
        overlineContent = {
            Column {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        supportingContent?.let {
                            Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    rating?.takeIf { it > 0.0 }?.let { RatingStars(it, starSize = 12.dp) }
                }
                progress?.also {
                    NiceLinearProgressIndicator(progress = { it })
                }
            }
        },
        leadingContent = {
            if (thumbnail != null || placeholderIcon != null) {
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.size(thumbnailSize).padding(thumbnailPadding),
                ) {
                    if (thumbnail != null) Image(
                        bitmap = thumbnail,
                        contentDescription = null,
                        contentScale = when {
                            thumbnailSize.height == Dp.Unspecified -> ContentScale.FillWidth
                            else -> ContentScale.Crop
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    else if (placeholderIcon != null) Icon(
                        imageVector = placeholderIcon,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        tint = LocalContentColor.current.copy(alpha = 0.75f),
                    )
                }
            }
        },
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .then(
                if (isCurrentItem) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                ) else Modifier
            ),
    )
}
