package me.likenotesapp.requests

import me.likenotesapp.Note
import me.likenotesapp.UpdatableState

sealed class ToPlatform() : IRequest {
    data class AddNote(
        val note: Note,
        override val response: UpdatableState<Boolean> = UpdatableState(false)
    ) : ToPlatform(), IResponsable<Boolean>

    data class UpdateNote(
        val note: Note,
        override val response: UpdatableState<Boolean> = UpdatableState(false)
    ) : ToPlatform(), IResponsable<Boolean>

    data class GetNotes(
        override val response: UpdatableState<List<Note>> = UpdatableState(emptyList())
    ) : ToPlatform(), IResponsable<List<Note>>
}