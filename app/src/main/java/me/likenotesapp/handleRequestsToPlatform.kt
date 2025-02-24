package me.likenotesapp

import android.app.Application
import me.likenotesapp.database.NotesDatabase
import me.likenotesapp.requests.ToPlatform

fun handleRequestsToPlatform(context: Application) {
    val notesDatabase = NotesDatabase.init(context)

    Platform.request.listen { request ->
        actualScope.launchWithHandler {
            when (request) {
                is ToPlatform.AddNote -> {
                    notesDatabase.noteDao().insert(request.note)
                    request.response.post(Unit)
                }

                is ToPlatform.RemoveNote -> {
                    notesDatabase.noteDao().delete(request.note)
                    request.response.post(Unit)
                }

                is ToPlatform.UpdateNote -> {
                    notesDatabase.noteDao().update(request.note)
                    request.response.post(Unit)
                }

                is ToPlatform.GetNotes -> {
                    val notesFlow = notesDatabase.noteDao().getAllNotes()
                    notesFlow.collect { notes ->
                        request.response.post(notes)
                    }
                }

                else -> {
                    // do nothing
                }
            }
        }
    }
}