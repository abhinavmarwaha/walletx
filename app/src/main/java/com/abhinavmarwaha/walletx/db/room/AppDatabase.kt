package com.abhinavmarwaha.walletx.db.room

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

const val DATABASE_NAME = "rssDatabase"

@Database(entities = [Card::class, CardGroup::class, CardGroupRelation::class, KeyValueNote::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDAO
    abstract fun cardGroupDao(): CardGroupDAO
    abstract fun cgRelationDao(): CGRelationDao
    abstract fun notesDao(): NotesDao

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
                .addCallback(object: Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ioThread {
                            val note = KeyValueNote()
                            note.title = "Main"
                            note.id = 0
                            note.keyValues = listOf(Pair("Name", ""))
                            getInstance(context).notesDao().insertNote(note)
                        }
                }})
                .build()
        }
    }
}

