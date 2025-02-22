package me.likenotesapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.likenotesapp.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        fun init(context: Context): NotesDatabase {
            return Room.databaseBuilder(context, NotesDatabase::class.java, "note_db")
                .build()
        }
    }
}