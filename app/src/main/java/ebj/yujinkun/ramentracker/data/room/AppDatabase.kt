package ebj.yujinkun.ramentracker.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(entities = [RamenEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ramenDao(): RamenDao

    companion object {
        private const val DB_NAME = "app-db"

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(@ApplicationContext context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .build()
        }
    }
}