package me.likenotesapp

import androidx.compose.runtime.toMutableStateList
import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.requests.IRequest
import me.likenotesapp.requests.platform.Platform
import me.likenotesapp.requests.platform.ToPlatform
import me.likenotesapp.requests.user.ToUser
import me.likenotesapp.requests.user.User

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

fun <T : Any> IRequest<T>.onResponse(content: (T) -> Unit) {
    debug {
        println(this)
    }

    response.onEach {
        content(it)
    }

    when (this) {
        is ToUser -> User.request.next(this)
        is ToPlatform -> Platform.request.next(this)
    }
}

fun appFunction() {
    ToUser.PostMessage(
        message = "Рад вас видеть снова :)",
        actionName = "Привет!"
    ).onResponse {
        ToUser.GetChoice(
            title = "Чего пожелаете?",
            items = MainChoice.entries
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
                ToUser.PostLoadingMessage("Идет загрузка...")
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
                        actionName = "Океюшки"
                    ).onResponse {
                        ToUser.PostMessage(
                            message = "Заметка сохранена",
                            actionName = "21312"
                        ).onResponse {
                            appFunction()
                        }
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
                        readNotes()
                    }

                is NotesChoice.Select -> editNote(choice.note)
            }
        }
    }
}