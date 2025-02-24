package me.likenotesapp.requests

import me.likenotesapp.Note
import me.likenotesapp.UpdatableState


sealed class ToPlatform() {

    data class AddNote(
        val note: Note,
        override val response: UpdatableState<Unit> = UpdatableState<Unit>()
    ) : ToPlatform(), IRequest<Unit>

    data class RemoveNote(
        val note: Note,
        override val response: UpdatableState<Unit> = UpdatableState<Unit>()
    ) : ToPlatform(), IRequest<Unit>


    data class UpdateNote(
        val note: Note,
        override val response: UpdatableState<Unit> = UpdatableState<Unit>()
    ) : ToPlatform(), IRequest<Unit>

    data class GetNotes(
        override val response: UpdatableState<List<Note>> = UpdatableState<List<Note>>()
    ) : ToPlatform(), IRequest<List<Note>>
}