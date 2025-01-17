package us.huseli.kiddo.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R

@Composable
fun MediaDetailsBanner(
    title: String,
    headlineStyle: TextStyle,
    thumbnailSize: DpSize = DpSize(width = 140.dp, height = 140.dp),
    thumbnailContentScale: ContentScale = ContentScale.FillWidth,
    subTitle: String? = null,
    rating: Double? = null,
    banner: ImageBitmap? = null,
    thumbnail: ImageBitmap? = null,
    votes: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        // modifier = modifier.height(160.dp)
        modifier = modifier
            .heightIn(max = thumbnailSize.height + 20.dp)
            .height(IntrinsicSize.Min)
    ) {
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            thumbnail?.also {
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = Color.Transparent,
                    modifier = Modifier.size(thumbnailSize)
                ) {
                    Image(
                        bitmap = it,
                        contentScale = thumbnailContentScale,
                        contentDescription = null,
                        modifier = Modifier
                            .shadow(5.dp)
                            .fillMaxWidth()
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
                    .shadow(5.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = title,
                        style = headlineStyle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    Row {
                        subTitle?.also {
                            Text(
                                text = it,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }
                    }

                    Row {
                        rating?.takeIf { it > 0.0 }?.also { rating ->
                            Column(modifier = Modifier.padding(vertical = 5.dp)) {
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
        }
    }
}
