package me.likenotesapp

import androidx.compose.runtime.toMutableStateList
import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.developer.primitives.requests.request
import me.likenotesapp.developer.primitives.requests.platform.ToPlatform
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.developer.primitives.requests.user.User

interface IChoice

class Back : IChoice
class Cancel : IChoice

interface IMenuItem : IChoice {
    val text: String
}

enum class MainMenu(override val text: String) : IMenuItem {
    AddNote("Написать заметку"),
    ReadNotes("Читать заметки"),
    SearchNotes("Искать заметки"),
    Trash("Корзина"),
}

enum class TrashMenu(override val text: String) : IMenuItem {
    ReadNotes("Читать заметки"),
    SearchNotes("Искать заметки"),
    EmptyTrash("Очистить корзину"),
}

sealed class NotesChoice() : IChoice {
    data class Remove(val note: Note) : NotesChoice()
    data class Select(val note: Note) : NotesChoice()
}

fun main() {
    ToUser.GetChoice(
        title = "Блокнот",
        items = MainMenu.entries,
        canBack = false
    ).request { choice ->
        when (choice) {
            MainMenu.AddNote -> {
                editNote()
            }

            MainMenu.ReadNotes -> {
                ToPlatform.GetNotes().request { notes ->
                    readNotes(notes)
                }
            }

            MainMenu.SearchNotes -> {
                searchNotes()
            }

            MainMenu.Trash -> {
                ToPlatform.GetNotes(removed = true).request { removedNotes ->
                    openTrash(removedNotes)
                }
            }
        }
    }
}

private fun openTrash(removedNotes: List<Note>) {
    ToUser.GetChoice(
        title = "Заметок в корзине: ${removedNotes.size}",
        items = TrashMenu.entries
    ).request { choice ->
        when (choice) {
            is Back -> {
                User.requestPrevious()
            }

            TrashMenu.EmptyTrash -> {
                removedNotes.forEach { note ->
                    ToPlatform.RemoveNote(note)
                        .request {
                            ToUser.PostMessage(
                                message = "Корзина очищена",
                                actionName = "Хорошо"
                            ).request {
                                ToPlatform.GetNotes(removed = true).request { removedNotes ->
                                    User.request.pop()
                                    User.request.pop()
                                    openTrash(removedNotes)
                                }
                            }
                        }
                }
            }

            TrashMenu.ReadNotes -> {
                readNotes(removedNotes, "Заметки в корзине")
            }

            TrashMenu.SearchNotes -> {
                searchNotes(removed = true)
            }
        }
    }
}

fun searchNotes(removed: Boolean = false) {
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
                ToPlatform.GetNotes(query = response, removed = removed).request { notes ->
                    readNotes(notes, "Заметки по запросу \n\n\"$response\"")
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

fun readNotes(notes: List<Note>, title: String = "Заметки") {
    ToUser.GetChoice(
        title = title,
        items = notes.toMutableStateList(),
    ).request { response ->
        debug {
            println("choice: $response")
        }

        when (response) {
            is Back -> User.requestPrevious()
            is NotesChoice.Remove -> {
                response.note.removed = true
                ToPlatform.UpdateNote(response.note)
                    .request {
                        User.request.pop()
                        ToPlatform.GetNotes().request { notes ->
                            readNotes(notes, title)
                        }
                    }
            }

            is NotesChoice.Select -> editNote(response.note)
        }
    }
}