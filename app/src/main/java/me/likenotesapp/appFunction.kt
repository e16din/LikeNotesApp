package me.likenotesapp

import androidx.compose.runtime.toMutableStateList
import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.developer.primitives.requests.onResponse
import me.likenotesapp.developer.primitives.requests.platform.ToPlatform
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.developer.primitives.requests.user.User

enum class MainChoice(val text: String) {
    AddNote("Добавить заметку"),
    ReadNotes("Читать заметки")
}

interface IChoice

class Back : IChoice
class Cancel : IChoice

sealed class NotesChoice() : IChoice {
    data class Remove(val note: Note) : NotesChoice()
    data class Select(val note: Note) : NotesChoice()
}

fun appFunction() {
    ToUser.GetChoice(
        title = "Блокнот",
        items = MainChoice.entries,
        canBack = false
    ).onResponse { choice ->
        when (choice) {
            is Back -> {
                User.requestPrevious()
            }

            MainChoice.AddNote -> {
                editNote()
            }

            MainChoice.ReadNotes -> {
                readNotes()
            }
        }
    }
}

fun editNote(initial: Note? = null) {
    ToUser.GetTextInput(
        title = "Заметка",
        label = "Заметка",
        initial = initial?.text,
        actionName = "Сохранить"
    ).onResponse { input ->
        when (input) {
            is Back -> {
                User.requestPrevious()
            }

            is String -> {
                ToUser.PostLoadingMessage("В процессе...")
                    .onResponse { loading ->
                        if (loading is Cancel) {
                            User.requestPrevious()
                            return@onResponse
                        }
                    }

                val nowMs = System.currentTimeMillis()

                (if (initial == null) {
                    ToPlatform.AddNote(
                        Note(
                            text = input,
                            createdMs = nowMs,
                            updatedMs = nowMs
                        )
                    )

                } else {
                    ToPlatform.UpdateNote(initial.apply {
                        text = input
                        updatedMs = nowMs
                    })
                }).onResponse {
                    ToUser.PostMessage(
                        message = "Заметка сохранена",
                        actionName = "Хорошо"
                    ).onResponse {
                        User.request.values.clear()
                        appFunction()
                    }
                }
            }
        }
    }
}

fun readNotes() {
    ToPlatform.GetNotes().onResponse { notes ->
        ToUser.GetChoice(
            title = "Заметки",
            items = notes.toMutableStateList(),
        ).onResponse { choice ->
            debug {
                println("choice: $choice")
            }

            when (choice) {
                is Back -> User.requestPrevious()
                is NotesChoice.Remove -> ToPlatform.RemoveNote(choice.note)
                    .onResponse {
                        User.request.pop()
                        readNotes()
                    }

                is NotesChoice.Select -> editNote(choice.note)
            }
        }
    }
}