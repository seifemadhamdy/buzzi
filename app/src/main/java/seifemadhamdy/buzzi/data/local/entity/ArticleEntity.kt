package seifemadhamdy.buzzi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
  @PrimaryKey val url: String,
  val sourceName: String?,
  val author: String?,
  val title: String,
  val description: String?,
  val imageUrl: String?,
  val publishedAt: String?,
  val content: String?,
  val cachedAt: Long = System.currentTimeMillis(),
)
