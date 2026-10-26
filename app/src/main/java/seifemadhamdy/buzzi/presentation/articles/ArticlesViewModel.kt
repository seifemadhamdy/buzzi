package seifemadhamdy.buzzi.presentation.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import seifemadhamdy.buzzi.domain.usecase.GetTopHeadlinesUseCase
import seifemadhamdy.buzzi.domain.usecase.SearchArticlesUseCase
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getTopHeadlines: GetTopHeadlinesUseCase,
    private val searchArticles: SearchArticlesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<ArticlesEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    private var contentJob: Job? = null

    fun onIntent(intent: ArticlesIntent) {
        when (intent) {
            is ArticlesIntent.LoadArticles, ArticlesIntent.Retry -> loadCurrentContent()
            is ArticlesIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is ArticlesIntent.ArticleSelected -> effectChannel.trySend(
                ArticlesEffect.NavigateToDetails(
                    intent.article
                )
            )
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                errorMessage = null,
            )
        }

        contentJob?.cancel()
        _uiState.update { it.copy(isLoading = true) }

        contentJob = viewModelScope.launch {
            delay(350.milliseconds)
            fetchContent(query)
        }
    }

    private fun loadCurrentContent() {
        contentJob?.cancel()

        val query = _uiState.value.query

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        contentJob = viewModelScope.launch {
            fetchContent(query)
        }
    }

    private suspend fun fetchContent(query: String) {
        try {
            val articles = if (query.isBlank()) {
                getTopHeadlines()
            } else {
                searchArticles(query)
            }

            _uiState.update {
                it.copy(
                    articles = articles,
                    isLoading = false,
                    errorMessage = null
                )
            }
        } catch (exception: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = exception.toUserMessage()
                )
            }
        }
    }

    private fun Exception.toUserMessage(): String =
        when (this) {
            is java.io.IOException ->
                "Couldn't connect. Check your internet connection and try again."

            else ->
                "Something went wrong. Please try again."
        }
}