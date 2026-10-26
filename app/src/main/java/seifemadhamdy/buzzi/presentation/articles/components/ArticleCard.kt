package seifemadhamdy.buzzi.presentation.articles.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import kotlinx.coroutines.launch
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.presentation.common.paletteFrom

@Composable
fun ArticleCard(
  article: Article,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val animationSpec = tween<Color>(durationMillis = 800)

  var bottomContentHeight by rememberSaveable {
    mutableIntStateOf(0)
  }

  var isImageLight by
    rememberSaveable(article.imageUrl) {
      mutableStateOf(false)
    }

  var gradientColorArgb by
    rememberSaveable(article.imageUrl) {
      mutableStateOf<Int?>(null)
    }

  val gradientColor = gradientColorArgb?.let { Color(it) }

  val contentColor by
    animateColorAsState(
      targetValue = if (isImageLight) Color.Black else Color.White,
      animationSpec = animationSpec,
      label = "contentColor",
    )

  val pillColor = contentColor.copy(alpha = 0.2f)

  val animatedGradient by
    animateColorAsState(
      targetValue = gradientColor ?: Color.Black,
      animationSpec = animationSpec,
      label = "gradientColor",
    )

  val imageRequest =
    remember(article.imageUrl) {
      ImageRequest.Builder(context).data(article.imageUrl).crossfade(true).build()
    }

  val maskColors = remember {
    listOf(Color.Transparent, Color.Black)
  }

  Card(
    modifier = modifier.fillMaxWidth().aspectRatio(4f / 5f).clickable(onClick = onClick),
    shape = MaterialTheme.shapes.extraLarge,
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Box(Modifier.fillMaxSize()) {
      AsyncImage(
        model = imageRequest,
        contentDescription = article.title,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        onSuccess = { state ->
          val bitmap = state.result.image.toBitmap()
          scope.launch {
            paletteFrom(bitmap)?.let {
              isImageLight = it.isLight
              gradientColorArgb = it.gradientColor?.toArgb()
            }
          }
        },
      )

      if (article.imageUrl != null) {
        AsyncImage(
          model = imageRequest,
          contentDescription = null,
          modifier =
            Modifier.fillMaxSize()
              .blur(40.dp)
              .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
              }
              .drawWithCache {
                val paddingPx = 20.dp.toPx()
                val fadeHeightPx = 60.dp.toPx()
                val contentTop = size.height - bottomContentHeight - paddingPx

                val mask =
                  Brush.verticalGradient(
                    colors = maskColors,
                    startY = (contentTop - fadeHeightPx).coerceAtLeast(0f),
                    endY = contentTop,
                  )

                onDrawWithContent {
                  drawContent()
                  drawRect(
                    brush = mask,
                    blendMode = BlendMode.DstIn,
                  )
                }
              },
          contentScale = ContentScale.Crop,
        )
      }

      CompositionLocalProvider(LocalContentColor provides contentColor) {
        Column(
          modifier =
            Modifier.fillMaxSize()
              .drawWithCache {
                val overlay =
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        Color.Transparent,
                        animatedGradient.copy(alpha = 0.1f),
                        animatedGradient.copy(alpha = 0.3f),
                        animatedGradient.copy(alpha = 0.6f),
                      )
                  )

                onDrawBehind {
                  drawRect(overlay)
                }
              }
              .padding(24.dp)
        ) {
          Spacer(Modifier.weight(1f))

          Column(
            modifier =
              Modifier.onSizeChanged {
                bottomContentHeight = it.height
              }
          ) {
            Surface(
              shape = CircleShape,
              color = pillColor,
              contentColor = contentColor,
              modifier = Modifier.padding(bottom = 12.dp),
            ) {
              Text(
                text = article.sourceName?.uppercase() ?: "LATEST",
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                modifier =
                  Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                  ),
              )
            }

            Text(
              text = article.title,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.SemiBold,
              maxLines = 3,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(bottom = 8.dp),
            )

            article.author?.takeIf(String::isNotBlank)?.let { author ->
              Text(
                text = "By $author",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }
    }
  }
}
