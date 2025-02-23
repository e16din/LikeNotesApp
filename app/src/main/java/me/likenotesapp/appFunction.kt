package me.likenotesapp

import androidx.compose.runtime.toMutableStateList
import me.likenotesapp.requests.ToPlatform
import me.likenotesapp.requests.ToUser

enum class MainChoice(val text: String) {
    AddNote("Добавить заметку"),
    ReadNotes("Читать заметки")
}

sealed class NotesChoice() {
    class Back : NotesChoice()
    data class Remove(val note: Note) : NotesChoice()
    data class Select(val note: Note) : NotesChoice()
}

suspend fun appFunction() {
    suspend fun reset() {
        User.clear()
        Platform.clear()
        appFunction()
    }

    suspend fun editNote(initial: Note? = null) {
        User.request<String>(
            ToUser.GetTextInput(
                label = "Заметка",
                initial = initial?.text,
                actionName = "Сохранить"

            ) { noteText ->
                User.requestNotBlocked(
                    ToUser.PostLoadingMessage("Идет загрузка...")
                )

                val nowMs = System.currentTimeMillis()
                Platform.request<Boolean>(
                    if (initial == null) {
                        val note = Note(
                            text = noteText,
                            createdMs = nowMs,
                            updatedMs = nowMs
                        )
                        ToPlatform.AddNote(note)

                    } else {
                        ToPlatform.UpdateNote(initial.apply {
                            text = noteText
                            updatedMs = nowMs
                        })

                    }.apply {
                        onResponse = { success ->
                            val message = if (success)
                                "Сохранение прошло успешно!"
                            else
                                "Возникла ошибка при сохранении :("
                            User.request<Unit>(
                                ToUser.PostMessage(
                                    message = message,
                                    actionName = "Океюшки"
                                ) {
                                    reset()
                                })
                        }
                    }
                )
            }
        )
    }

    suspend fun readNotes() {
        Platform.request<List<Note>>(ToPlatform.GetNotes { notes ->
            User.request<Any>(
                ToUser.GetChoice<Note>(
                    title = "Заметки",
                    items = notes.toMutableStateList(),
                )
                { choice ->
                    println("choice: $choice")
                    when (choice) {

                        is NotesChoice.Back -> User.requestPrevious()
                        is NotesChoice.Remove -> Platform.request<Boolean>(

                            ToPlatform.RemoveNote(choice.note) {
                                readNotes()
                            }
                        )

                        is NotesChoice.Select -> editNote(choice.note)
                    }
                })
        })
    }

    User.request<Unit>(
        ToUser.PostMessage(
            message = "Рад вас видеть снова :)",
            actionName = "Привет!"
        )
        {
            User.request<MainChoice>(
                ToUser.GetChoice(
                    title = "Чего пожелаете?",
                    items = MainChoice.entries
                )
                { item ->
                    when (item) {
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