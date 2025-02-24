package me.likenotesapp

import android.app.Application
import me.likenotesapp.database.NotesDatabase
import me.likenotesapp.requests.ToPlatform

fun handleRequestsToPlatform(context: Application) {
    val notesDatabase = NotesDatabase.init(context)

    Platform.requestUpdatable.listen { request ->
        actualScope.launchWithHandler {
            when (request) {
                is ToPlatform.AddNote -> {
                    notesDatabase.noteDao().insert(request.note)
                    Platform.response.post(true)
                }

                is ToPlatform.RemoveNote -> {
                    notesDatabase.noteDao().delete(request.note)
                    Platform.response.post(true)
                }

                is ToPlatform.UpdateNote -> {
                    notesDatabase.noteDao().update(request.note)
                    Platform.response.post(true)
                }

                is ToPlatform.GetNotes -> {
                    val notesFlow = notesDatabase.noteDao().getAllNotes()
                    notesFlow.collect { notes ->
                        Platform.response.post(notes)
                    }
                }
            }
        }
    }
}