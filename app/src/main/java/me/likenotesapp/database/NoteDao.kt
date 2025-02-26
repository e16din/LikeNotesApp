package me.likenotesapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import me.likenotesapp.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(item: Note)

    @Update
    suspend fun update(item: Note)

    @Delete
    suspend fun delete(item: Note)

    @Query("SELECT * from notes WHERE id = :id")
    fun getNote(id: Int): Note

    @Query("SELECT * from notes WHERE removed = :removed ORDER BY updatedMs DESC")
    fun getAllNotes(removed: Boolean): List<Note>

    @Query("SELECT * FROM notes WHERE removed = :removed AND text LIKE '%' || :query || '%'")
    fun searchNotes(query: String, removed: Boolean): List<Note>
}