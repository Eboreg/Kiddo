package us.huseli.kiddo.compose.screens.moviedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.RatingStars
import us.huseli.kiddo.data.types.VideoDetailsMovie

@Composable
fun MovieDetailsBanner(
    details: VideoDetailsMovie,
    headlineStyle: TextStyle,
    banner: ImageBitmap?,
    poster: ImageBitmap?,
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
            Box(modifier = Modifier.width(100.dp)) {
                poster?.also {
                    Image(
                        bitmap = it,
                        modifier = Modifier.shadow(5.dp),
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
                    Text(details.displayTitle, style = headlineStyle)
                    details.tagline?.also {
                        Text(text = it, maxLines = 2, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic)
                    }

                    details.rating?.takeIf { it > 0.0 }?.also { rating ->
                        Column {
                            RatingStars(rating)
                            details.votes?.also {
                                Text(
                                    text = stringResource(R.string.x_votes, details.votes),
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
