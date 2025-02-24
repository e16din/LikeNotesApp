package me.likenotesapp.requests

import me.likenotesapp.Note
import me.likenotesapp.UpdatableState


sealed class ToPlatform() : IRequest {
    data class AddNote(
        val note: Note,
        override var onResponse: (suspend (Note) -> Unit)? = null
    ) : ToPlatform(), IHasTypedResponse<Note>

    data class RemoveNote(
        val note: Note,
        override var onResponse: (suspend (Unit) -> Unit)? = null
    ) : ToPlatform(), IHasTypedResponse<Unit>


    data class UpdateNote(
        val note: Note,
        override var onResponse: (suspend (Unit) -> Unit)? = null
    ) : ToPlatform(), IHasTypedResponse<Unit>

    data class GetNotes(
        override var onResponse: (suspend (List<Note>) -> Unit)? = null
    ) : ToPlatform(), IHasTypedResponse<List<Note>>
}