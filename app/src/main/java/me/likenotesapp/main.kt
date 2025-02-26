package me.likenotesapp

import androidx.compose.runtime.toMutableStateList
import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.developer.primitives.requests.request
import me.likenotesapp.developer.primitives.requests.platform.ToPlatform
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.developer.primitives.requests.user.User

enum class MainChoice(val text: String) {
    AddNote("Написать заметку"),
    ReadNotes("Читать заметки"),
    SearchNote("Искать заметки")
}

interface IChoice

class Back : IChoice
class Cancel : IChoice

sealed class NotesChoice() : IChoice {
    data class Remove(val note: Note) : NotesChoice()
    data class Select(val note: Note) : NotesChoice()
}

fun main() {
    ToUser.GetChoice(
        title = "Блокнот",
        items = MainChoice.entries,
        canBack = false
    ).request { choice ->
        when (choice) {
            MainChoice.AddNote -> {
                editNote()
            }

            MainChoice.ReadNotes -> {
                readNotes()
            }

            MainChoice.SearchNote -> {
                ToUser.GetString(
                    title = "Поиск заметок",
                    label = "Искать строку",
                    actionName = "Найти",
                ).request { response ->
                    when (response) {
                        is Back -> {
                            User.requestPrevious()
                        }

                        is String -> {
                            readNotes(query = response, title = "Заметки по запросу \n\n\"$response\"")
                        }
                    }
                }
            }
        }
    }
}

fun editNote(initial: Note? = null) {
    ToUser.GetString(
        title = "Заметка",
        label = "Заметка",
        data = initial?.text,
        actionName = "Сохранить",
        type = ToUser.GetString.Type.Long
    ).request { response ->
        when (response) {
            is Back -> {
                User.requestPrevious()
            }

            is String -> {
                ToUser.PostMessage("В процессе...", type = ToUser.PostMessage.Type.Loading)
                    .request { loading ->
                        if (loading is Cancel) {
                            User.requestPrevious()
                            return@request
                        }
                    }

                val nowMs = System.currentTimeMillis()

                (if (initial == null) {
                    ToPlatform.AddNote(
                        Note(
                            text = response,
                            createdMs = nowMs,
                            updatedMs = nowMs
                        )
                    )

                } else {
                    ToPlatform.UpdateNote(initial.apply {
                        text = response
                        updatedMs = nowMs
                    })
                }).request {
                    ToUser.PostMessage(
                        message = "Заметка сохранена",
                        actionName = "Хорошо"
                    ).request {
                        User.request.values.clear()
                        main()
                    }
                }
            }
        }
    }
}

fun readNotes(query: String = "", title: String = "Заметки") {
    ToPlatform.GetNotes(query).request { notes ->
        ToUser.GetChoice(
            title = title,
            items = notes.toMutableStateList(),
        ).request { response ->
            debug {
                println("choice: $response")
            }

            when (response) {
                is Back -> User.requestPrevious()
                is NotesChoice.Remove -> ToPlatform.RemoveNote(response.note)
                    .request {
                        User.request.pop()
                        readNotes()
                    }

                is NotesChoice.Select -> editNote(response.note)
            }
        }
    }
}