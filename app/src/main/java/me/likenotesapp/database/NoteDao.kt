package me.likenotesapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
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
    fun getNote(id: Int): Flow<Note>

    @Query("SELECT * from notes ORDER BY updatedMs DESC")
    fun getAllNotes(): Flow<List<Note>>
}