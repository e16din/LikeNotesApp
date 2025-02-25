package me.likenotesapp

import android.app.Application
import me.likenotesapp.database.NotesDatabase
import me.likenotesapp.developer.primitives.work
import me.likenotesapp.requests.platform.Platform
import me.likenotesapp.requests.platform.ToPlatform

fun handleRequestsToPlatform(context: Application) {
    val notesDatabase = NotesDatabase.init(context)

    Platform.request.onEach { request ->
        when (request) {
            is ToPlatform.AddNote -> {
                work(onDone = {
                    request.response.next(Unit)
                }) {
                    notesDatabase.noteDao().insert(request.note)
                }
            }

            is ToPlatform.RemoveNote -> {
                work(onDone = {
                    request.response.next(Unit)
                }) {
                    notesDatabase.noteDao().delete(request.note)
                }
            }

            is ToPlatform.UpdateNote -> {
                work(onDone = {
                    request.response.next(Unit)
                }) {
                    notesDatabase.noteDao().update(request.note)
                }
            }

            is ToPlatform.GetNotes -> {
                work(onDone = { notes ->
                    request.response.next(notes)
                }) {
                    return@work notesDatabase.noteDao().getAllNotes()
                }
            }
        }
    }
}