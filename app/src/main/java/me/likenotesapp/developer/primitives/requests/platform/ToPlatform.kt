package me.likenotesapp.developer.primitives.requests.platform

import me.likenotesapp.Note
import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.developer.primitives.requests.IRequest


sealed class ToPlatform() {

    data class AddNote(
        val note: Note,
        override val response: ForEach<Unit> = ForEach<Unit>()
    ) : ToPlatform(), IRequest<Unit>

    data class RemoveNote(
        val note: Note,
        override val response: ForEach<Unit> = ForEach<Unit>()
    ) : ToPlatform(), IRequest<Unit>

    data class UpdateNote(
        val note: Note,
        override val response: ForEach<Unit> = ForEach<Unit>()
    ) : ToPlatform(), IRequest<Unit>

    data class GetNotes(
        override val response: ForEach<List<Note>> = ForEach<List<Note>>()
    ) : ToPlatform(), IRequest<List<Note>>
}