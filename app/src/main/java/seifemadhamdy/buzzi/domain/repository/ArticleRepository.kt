package seifemadhamdy.buzzi.domain.repository

import seifemadhamdy.buzzi.domain.model.Article

interface ArticleRepository {

    suspend fun getTopHeadlines(): List<Article>

    suspend fun searchArticles(query: String): List<Article>

    suspend fun getArticleByUrl(url: String): Article?
}