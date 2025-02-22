package me.likenotesapp

import android.app.Application
import kotlinx.coroutines.GlobalScope
import me.likenotesapp.database.NotesDatabase
import me.likenotesapp.requests.ToPlatform

fun handleRequestsToPlatform(context: Application) {
    val notesDatabase = NotesDatabase.init(context)

    Platform.currentRequest.listen { request ->
        GlobalScope.launchWithHandler {
            when (request) {
                is ToPlatform.AddNote -> {
                    notesDatabase.noteDao().insert(request.note)
                    request.response.post(true)
                }

                is ToPlatform.UpdateNote -> {
                    notesDatabase.noteDao().update(request.note)
                    request.response.post(true)
                }

                is ToPlatform.GetNotes -> {
                    val notesFlow = notesDatabase.noteDao().getAllNotes()
                    notesFlow.collect { notes ->
                        request.response.post(notes)
                    }
                }
            }
        }
    }
}