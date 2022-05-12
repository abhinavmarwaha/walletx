package com.abhinavmarwaha.walletx.db.room

import android.content.Context
import androidx.room.*
import java.net.URL


const val DATABASE_NAME = "rssDatabase"

@Database(entities = [Feed::class, FeedItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun feedDao(): FeedDAO
    abstract fun feedItemDao(): FeedItemDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabaseClient(context: Context): AppDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, AppDatabase::class.java, "APP_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }


        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}

class Converters {
    @TypeConverter
    public fun dateTimeFromString(value: String?): ZonedDateTime? {
        var dt: ZonedDateTime? = null
        if (value != null) {
            try {
                dt = ZonedDateTime.parse(value)
            } catch (t: Throwable) {
            }
        }
        return dt
    }

    @TypeConverter
    public fun stringFromDateTime(value: ZonedDateTime?): String? =
        value?.toString()

    @TypeConverter
    public fun fromLong(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    public fun instantToLong(instant: Instant): Long {
        return instant.toEpochMilli()
    }

    @TypeConverter
    public fun toURL(value: String?): URL? {
        return if (value == null) null else URL(value)
    }

    @TypeConverter
    public fun fromURL(url: URL?): String? {
        return url?.toString()
    }
}
