package seifemadhamdy.buzzi.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import seifemadhamdy.buzzi.domain.repository.ArticleRepository
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel
@Inject
constructor(
  savedStateHandle: SavedStateHandle,
  private val repository: ArticleRepository,
) : ViewModel() {

  private val articleUrl: String = checkNotNull(savedStateHandle["url"])

  private val _uiState = MutableStateFlow(ArticleDetailsUiState())
  val uiState = _uiState.asStateFlow()

  init {
    loadArticle()
  }

  private fun loadArticle() {
    viewModelScope.launch {
      try {
        val article = repository.getArticleByUrl(articleUrl)

        _uiState.value =
          if (article != null) {
            ArticleDetailsUiState(
              article = article,
              isLoading = false,
            )
          } else {
            ArticleDetailsUiState(
              isLoading = false,
              errorMessage = "This article is no longer available.",
            )
          }
      } catch (exception: CancellationException) {
        throw exception
      } catch (exception: Exception) {
        _uiState.value =
          ArticleDetailsUiState(
            isLoading = false,
            errorMessage = "Couldn't load this article. Please try again.",
          )
      }
    }
  }
}
