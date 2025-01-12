package us.huseli.kiddo.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddToQueue
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R

@Composable
fun MediaDetailsBanner(
    title: String,
    headlineStyle: TextStyle,
    thumbnailWidth: Dp,
    subTitle: String? = null,
    rating: Double? = null,
    banner: ImageBitmap? = null,
    thumbnail: ImageBitmap? = null,
    votes: String? = null,
    onPlayClick: () -> Unit,
    onEnqueueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.height(160.dp)) {
        banner?.also {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.5f,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(thumbnailWidth)) {
                thumbnail?.also {
                    Image(
                        bitmap = it,
                        modifier = Modifier.shadow(5.dp).fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                    )
                }
            }

            Card(
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(5.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(title, style = headlineStyle, maxLines = 2, overflow = TextOverflow.Ellipsis)

                    Column { subTitle?.also { Text(text = it, maxLines = 2, overflow = TextOverflow.Ellipsis) } }

                    Column {
                        rating?.takeIf { it > 0.0 }?.also { rating ->
                            RatingStars(rating)
                            votes?.takeIf { it != "-1" }?.also {
                                Text(
                                    text = stringResource(R.string.x_votes, it),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            FilledIconButton(
                onClick = onEnqueueClick,
                content = { Icon(Icons.Sharp.AddToQueue, stringResource(R.string.add_to_queue)) },
                modifier = Modifier.shadow(10.dp),
            )
            FilledIconButton(
                onClick = onPlayClick,
                content = { Icon(Icons.Sharp.PlayArrow, stringResource(R.string.play)) },
                modifier = Modifier.shadow(10.dp),
            )
        }
    }
}
