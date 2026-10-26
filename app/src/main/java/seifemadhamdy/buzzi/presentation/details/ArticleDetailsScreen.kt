package seifemadhamdy.buzzi.presentation.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import kotlinx.coroutines.launch
import seifemadhamdy.buzzi.core.util.toFriendlyDate
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.presentation.common.paletteFrom

private val ScreenPadding = 20.dp
private val BackdropHeight = 520.dp
private val TruncatedSuffix = Regex("""\s*\[\+\d+ chars]\s*$""")

@Composable
fun ArticleDetailsScreen(
  article: Article,
  onBack: () -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val colorSpec = tween<Color>(durationMillis = 800)
  val colors = MaterialTheme.colorScheme

  var gradientColorArgb by
    rememberSaveable(article.imageUrl) {
      mutableStateOf<Int?>(null)
    }

  val gradientColor = gradientColorArgb?.let { Color(it) } ?: Color.Transparent

  val tint by
    animateColorAsState(
      targetValue = gradientColor,
      animationSpec = colorSpec,
      label = "tint",
    )

  val accent by
    animateColorAsState(
      targetValue =
        gradientColor.takeIf { it != Color.Transparent }?.let { lerp(it, colors.onSurface, 0.35f) }
          ?: colors.primary,
      animationSpec = colorSpec,
      label = "accent",
    )

  val imageRequest =
    remember(article.imageUrl) {
      ImageRequest.Builder(context).data(article.imageUrl).crossfade(true).build()
    }

  val body =
    remember(article.content) {
      article.content?.replace(TruncatedSuffix, "")?.trim()?.takeIf(String::isNotBlank)
    }

  val openArticle = {
    try {
      context.startActivity(Intent(Intent.ACTION_VIEW, article.url.toUri()))
    } catch (_: ActivityNotFoundException) {
      Toast.makeText(
          context,
          "No app can open this link.",
          Toast.LENGTH_SHORT,
        )
        .show()
    }
  }

  Box(modifier = Modifier.fillMaxSize().background(colors.surface)) {
    if (article.imageUrl != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      AsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier =
          Modifier.fillMaxWidth()
            .height(BackdropHeight)
            .blur(64.dp)
            .graphicsLayer {
              compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
              drawContent()
              drawRect(
                brush =
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        Color.Black.copy(alpha = 0.55f),
                        Color.Black.copy(alpha = 0.25f),
                        Color.Transparent,
                      )
                  ),
                blendMode = BlendMode.DstIn,
              )
            },
      )
    }

    Box(
      modifier =
        Modifier.fillMaxWidth().height(BackdropHeight).drawBehind {
          drawRect(
            Brush.verticalGradient(
              colors =
                listOf(
                  tint.copy(alpha = 0.35f),
                  tint.copy(alpha = 0.12f),
                  Color.Transparent,
                )
            )
          )
        }
    )

    Column(
      modifier =
        Modifier.fillMaxSize()
          .verticalScroll(rememberScrollState())
          .statusBarsPadding()
          .navigationBarsPadding()
          .padding(top = 64.dp, bottom = 40.dp)
    ) {
      if (article.imageUrl != null) {
        AsyncImage(
          model = imageRequest,
          contentDescription = article.title,
          contentScale = ContentScale.Crop,
          onSuccess = { state ->
            val bitmap = state.result.image.toBitmap()
            scope.launch {
              paletteFrom(bitmap)?.let {
                gradientColorArgb = it.gradientColor?.toArgb()
              }
            }
          },
          modifier =
            Modifier.padding(horizontal = ScreenPadding)
              .fillMaxWidth()
              .aspectRatio(16f / 9f)
              .clip(MaterialTheme.shapes.extraLarge),
        )
      }

      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPadding).padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            shape = CircleShape,
            color = colors.onSurface.copy(alpha = 0.1f),
            contentColor = colors.onSurface.copy(alpha = 0.8f),
          ) {
            Text(
              text = article.sourceName?.uppercase() ?: "LATEST",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              modifier =
                Modifier.padding(
                  horizontal = 12.dp,
                  vertical = 6.dp,
                ),
            )
          }

          article.publishedAt?.let { date ->
            Text(
              text = date.toFriendlyDate(),
              style = MaterialTheme.typography.labelLarge,
              color = colors.onSurface.copy(alpha = 0.6f),
            )
          }
        }

        Text(
          text = article.title,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = colors.onSurface,
        )

        article.author?.takeIf(String::isNotBlank)?.let { author ->
          Text(
            text = "By $author",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurface.copy(alpha = 0.7f),
          )
        }

        article.description?.takeIf(String::isNotBlank)?.let { description ->
          Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
              modifier = Modifier.width(4.dp).fillMaxHeight().clip(CircleShape).background(accent)
            )

            Spacer(Modifier.width(16.dp))

            Text(
              text = description,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Medium,
              lineHeight = 26.sp,
              color = colors.onSurface.copy(alpha = 0.85f),
            )
          }
        }

        body?.let { content ->
          SelectionContainer {
            Text(
              text = content,
              style = MaterialTheme.typography.bodyLarge,
              lineHeight = 28.sp,
              color = colors.onSurface.copy(alpha = 0.9f),
            )
          }
        }
      }
    }

    val buttonColors =
      IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = colors.surface.copy(alpha = 0.8f),
        contentColor = colors.onSurface,
      )

    Row(
      modifier =
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      FilledTonalIconButton(onClick = onBack, colors = buttonColors) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Navigate back",
        )
      }

      FilledTonalIconButton(onClick = openArticle, colors = buttonColors) {
        Icon(
          imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
          contentDescription = "Read original article",
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsRoute(
  onBack: () -> Unit,
  viewModel: ArticleDetailsViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when {
    uiState.isLoading -> {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    }

    uiState.article != null -> {
      ArticleDetailsScreen(
        article = uiState.article!!,
        onBack = onBack,
      )
    }

    else -> {
      Scaffold(
        topBar = {
          TopAppBar(
            title = { Text("Article") },
            navigationIcon = {
              IconButton(onClick = onBack) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
                )
              }
            },
          )
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = uiState.errorMessage ?: "Article unavailable.",
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      }
    }
  }
}
