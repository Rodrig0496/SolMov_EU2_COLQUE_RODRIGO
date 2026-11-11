package com.example.myapplication.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.dao.ProductDao
import com.example.myapplication.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "epis_store_catalog.db"
                ).addMigrations(object : androidx.room.migration.Migration(1, 2) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL("ALTER TABLE products ADD COLUMN locallyModified INTEGER NOT NULL DEFAULT 0")
                        db.execSQL("ALTER TABLE products ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
                        db.execSQL("ALTER TABLE products ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 0")
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
