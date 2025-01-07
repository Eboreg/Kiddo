package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.StarHalf
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material.icons.sharp.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

@Composable
fun RatingStars(rating: Double) {
    val totalhalfstars = rating.roundToInt()
    val fullstars = totalhalfstars / 2
    val halfstar = totalhalfstars % 2
    val emptystars = 5 - fullstars - halfstar

    Row {
        (1..fullstars).forEach { Icon(Icons.Sharp.Star, null, tint = Color.Yellow) }
        if (halfstar > 0) Icon(Icons.AutoMirrored.Sharp.StarHalf, null, tint = Color.Yellow)
        (1..emptystars).forEach { Icon(Icons.Sharp.StarBorder, null) }
    }
}
