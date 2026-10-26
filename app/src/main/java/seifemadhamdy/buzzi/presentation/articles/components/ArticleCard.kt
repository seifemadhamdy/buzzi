package seifemadhamdy.buzzi.presentation.articles.components

import android.graphics.Bitmap
import android.os.Build
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.ui.theme.BuzziTheme

@Composable
fun ArticleCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var bottomContentHeight by remember { mutableIntStateOf(0) }

    val defaultScrim = MaterialTheme.colorScheme.scrim

    var isImageLight by remember(article.imageUrl) { mutableStateOf(false) }
    var extractedGradientColor by remember(article.imageUrl) { mutableStateOf(defaultScrim) }

    val animationSpec = remember { tween<Color>(durationMillis = 800) }

    val animatedGradientColor by animateColorAsState(
        targetValue = extractedGradientColor,
        animationSpec = animationSpec,
        label = "gradientColor"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (isImageLight) Color(0xFF1C1B1F) else Color.White,
        animationSpec = animationSpec,
        label = "contentColor"
    )

    val animatedPillColor by animateColorAsState(
        targetValue = if (isImageLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.15f),
        animationSpec = animationSpec,
        label = "pillColor"
    )

    val imageRequest = remember(article.imageUrl) {
        ImageRequest.Builder(context)
            .data(article.imageUrl)
            .crossfade(true)
            .build()
    }

    val maskColors = remember { listOf(Color.Transparent, Color.Black) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageRequest,
                contentDescription = article.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { state ->
                    coroutineScope.launch(Dispatchers.Default) {
                        runCatching {
                            val bitmap = state.result.image.toBitmap()

                            val softwareBitmap =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && bitmap.config == Bitmap.Config.HARDWARE) {
                                    bitmap.copy(Bitmap.Config.ARGB_8888, false)
                                } else {
                                    bitmap
                                }

                            val palette = Palette.from(softwareBitmap!!).generate()

                            val dominantSwatch = palette.dominantSwatch ?: palette.vibrantSwatch
                            ?: palette.mutedSwatch
                            val light =
                                dominantSwatch?.rgb?.let { Color(it).luminance() > 0.5f } ?: false

                            isImageLight = light

                            val targetSwatch = if (light) {
                                palette.lightVibrantSwatch ?: palette.lightMutedSwatch
                                ?: dominantSwatch
                            } else {
                                palette.darkVibrantSwatch ?: palette.darkMutedSwatch
                                ?: dominantSwatch
                            }

                            targetSwatch?.rgb?.let { colorInt ->
                                extractedGradientColor = Color(colorInt)
                            } ?: run {
                                extractedGradientColor = if (light) Color.White else defaultScrim
                            }

                            if (softwareBitmap != bitmap) {
                                softwareBitmap.recycle()
                            }
                        }
                    }
                }
            )

            if (article.imageUrl != null) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 40.dp)
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithCache {
                            val paddingPx = 20.dp.toPx()
                            val fadeHeightPx = 60.dp.toPx()
                            val contentTop = size.height - bottomContentHeight - paddingPx
                            val startY = (contentTop - fadeHeightPx).coerceAtLeast(0f)

                            val maskGradient = Brush.verticalGradient(
                                colors = maskColors,
                                startY = startY,
                                endY = contentTop
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(brush = maskGradient, blendMode = BlendMode.DstIn)
                            }
                        },
                    contentScale = ContentScale.Crop,
                )
            }

            CompositionLocalProvider(LocalContentColor provides animatedContentColor) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            val scrim = animatedGradientColor
                            val overlayGradient = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    scrim.copy(alpha = 0.2f),
                                    scrim.copy(alpha = 0.6f),
                                    scrim.copy(alpha = 0.9f)
                                )
                            )
                            onDrawBehind {
                                drawRect(brush = overlayGradient)
                            }
                        }
                        .padding(24.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier.onSizeChanged { bottomContentHeight = it.height }
                    ) {
                        val sourceNameText = remember(article.sourceName) {
                            article.sourceName?.uppercase() ?: "LATEST"
                        }

                        Text(
                            text = sourceNameText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .graphicsLayer {
                                    shape = CircleShape
                                    clip = true
                                }
                                .drawBehind { drawRect(animatedPillColor) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val authorText = remember(article.author) {
                            article.author?.takeIf { it.isNotBlank() }?.let { "By $it" }
                        }

                        authorText?.let { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LocalContentColor.current.copy(alpha = 0.7f),
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

@Preview(showBackground = true)
@Composable
private fun ArticleCardPreview() {
    BuzziTheme {
        ArticleCard(
            article = Article(
                sourceName = "MotorTrend",
                author = "Jonny Lieberman",
                title = "Why the analogue driving experience is making a massive comeback",
                description = "In an era of hyper-connected, autonomous electric vehicles, a growing subculture of enthusiasts is demanding a return to tactile feedback, manual gearboxes, and raw mechanical connection.",
                url = "https://www.motortrend.com/features/analogue-driving-revival-2026",
                imageUrl = null,
                publishedAt = "2026-10-26T14:15:00Z",
                content = "Automakers are beginning to realize that the pursuit of ultimate lap times and zero-to-sixty metrics has stripped the joy from everyday driving. The latest wave of European sedans and Japanese coupes are trading digital complexity for mechanical purity, proving that sometimes, less intervention means more engagement.",
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}