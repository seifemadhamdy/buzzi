package seifemadhamdy.buzzi.presentation.common

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ImagePalette(
  val isLight: Boolean,
  val gradientColor: Color?,
)

private fun extractPalette(bitmap: Bitmap): ImagePalette {
  val palette = Palette.from(bitmap).generate()

  val dominant = palette.dominantSwatch ?: palette.vibrantSwatch ?: palette.mutedSwatch

  val isLight = dominant?.rgb?.let { Color(it).luminance() > 0.5f } ?: false

  val gradientSwatch =
    if (isLight) {
      palette.lightVibrantSwatch ?: palette.lightMutedSwatch ?: dominant
    } else {
      palette.darkVibrantSwatch ?: palette.darkMutedSwatch ?: dominant
    }

  return ImagePalette(
    isLight = isLight,
    gradientColor = gradientSwatch?.rgb?.let(::Color),
  )
}

suspend fun paletteFrom(bitmap: Bitmap): ImagePalette? =
  withContext(Dispatchers.Default) {
    val software =
      if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && bitmap.config == Bitmap.Config.HARDWARE
      ) {
        bitmap.copy(Bitmap.Config.ARGB_8888, false)
      } else {
        null
      }

    try {
      extractPalette(software ?: bitmap)
    } catch (_: Exception) {
      null
    } finally {
      software?.recycle()
    }
  }
