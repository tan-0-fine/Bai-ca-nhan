package com.example.api.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.api.Attachment
import com.example.api.Subtask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [TaskEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDB(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSubtaskList(value: List<Subtask>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSubtaskList(value: String): List<Subtask> {
        val type = object : TypeToken<List<Subtask>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromAttachmentList(value: List<Attachment>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAttachmentList(value: String): List<Attachment> {
        val type = object : TypeToken<List<Attachment>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}