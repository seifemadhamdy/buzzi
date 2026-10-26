package seifemadhamdy.buzzi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import seifemadhamdy.buzzi.data.local.entity.ArticleEntity

@Dao
interface ArticleDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertArticles(articles: List<ArticleEntity>)

  @Query("SELECT * FROM articles ORDER BY cachedAt DESC")
  suspend fun getArticles(): List<ArticleEntity>

  @Query(
    "SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY cachedAt DESC"
  )
  suspend fun searchArticles(query: String): List<ArticleEntity>

  @Query("SELECT * FROM articles WHERE url = :url LIMIT 1")
  suspend fun getArticleByUrl(url: String): ArticleEntity?

  @Query("DELETE FROM articles") suspend fun clearArticles()
}
