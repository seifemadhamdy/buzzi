package seifemadhamdy.buzzi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import seifemadhamdy.buzzi.data.local.dao.ArticleDao
import seifemadhamdy.buzzi.data.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
