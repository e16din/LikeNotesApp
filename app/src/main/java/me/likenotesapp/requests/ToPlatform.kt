package me.likenotesapp.requests

import me.likenotesapp.Note
import me.likenotesapp.UpdatableState

sealed class ToPlatform() : IRequest {
    data class AddNote(
        val note: Note,
        override val response: UpdatableState<Boolean> = UpdatableState(false),
        override var onResponse: (suspend (Boolean) -> Unit)? = null
    ) : ToPlatform(), IResponsable<Boolean>

    data class UpdateNote(
        val note: Note,
        override val response: UpdatableState<Boolean> = UpdatableState(false),
        override var onResponse: (suspend (Boolean) -> Unit)? = null
    ) : ToPlatform(), IResponsable<Boolean>

    data class GetNotes(
        override val response: UpdatableState<List<Note>> = UpdatableState(emptyList()),
        override var onResponse: (suspend (List<Note>) -> Unit)? = null
    ) : ToPlatform(), IResponsable<List<Note>>
}