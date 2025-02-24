package me.likenotesapp

import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.delay
import me.likenotesapp.requests.ToPlatform
import me.likenotesapp.requests.ToUser

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

suspend fun appFunction() {
    User.request(
        ToUser.PostMessage(
            message = "Рад вас видеть снова :)",
            actionName = "Привет!"
        )
        {
            User.request(
                ToUser.GetChoice(
                    title = "Чего пожелаете?",
                    items = MainChoice.entries
                )
                { choice ->
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
                })
        })
}

suspend fun resetTo() {
    User.clearAll()
    Platform.clearAll()
    appFunction()
}

suspend fun editNote(initial: Note? = null) {
    User.request(
        ToUser.GetTextInput(
            title = "Заметка",
            label = "Заметка",
            initial = initial?.text,
            actionName = "Сохранить"

        ) { input ->
            when (input) {
                is Back -> {
                    User.requestPrevious()
                }

                is String -> {
                    User.request(
                        ToUser.PostLoadingMessage("Идет загрузка...") { input ->
                            if (input is Cancel) {
                                User.requestPrevious()
                            }
                        },
                        isBlocked = false
                    )
                    delay(2000)

                    val nowMs = System.currentTimeMillis()
                    Platform.request<Unit>(
                        if (initial == null) {
                            val note = Note(
                                text = input,
                                createdMs = nowMs,
                                updatedMs = nowMs
                            )
                            ToPlatform.AddNote(note)

                        } else {
                            ToPlatform.UpdateNote(initial.apply {
                                text = input
                                updatedMs = nowMs
                            })

                        }.apply {
                            onResponse = {
                                val message = "Заметка сохранена"

                                User.request(
                                    ToUser.PostMessage(
                                        message = message,
                                        actionName = "Океюшки"
                                    ) {
                                        resetTo()
                                    })
                            }
                        }
                    )
                }
            }
        }
    )
}

suspend fun readNotes() {
    Platform.request<List<Note>>(
        ToPlatform.GetNotes { notes ->
            User.request(
                ToUser.GetChoice(
                    title = "Заметки",
                    items = notes.toMutableStateList(),
                )
                { choice ->
                    println("choice: $choice")
                    when (choice) {
                        is Back -> User.requestPrevious()
                        is NotesChoice.Remove -> Platform.request<Unit>(
                            ToPlatform.RemoveNote(choice.note) {
                                readNotes()
                            }
                        )

                        is NotesChoice.Select -> editNote(choice.note)
                    }
                })
        })
}