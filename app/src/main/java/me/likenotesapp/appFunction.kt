package me.likenotesapp

import me.likenotesapp.requests.ToPlatform
import me.likenotesapp.requests.ToUser

enum class MainIntent(val text: String) {
    AddNote("Добавить заметку"),
    ReadNotes("Читать заметки")
}

suspend fun appFunction() {
    suspend fun reset() {
        User.clear()
        Platform.clear()
        appFunction()
    }

    suspend fun editNote(initial:Note? = null) {
        User.request<String>(
            ToUser.GetTextInput(
                label = "Заметка",
                initial = initial?.text,
                actionName = "Сохранить"
            )
        ) { noteText ->
            User.requestNotBlocked(
                ToUser.PostLoadingMessage("Идет загрузка...")
            )

            val nowMs = System.currentTimeMillis()

            val request = if(initial == null) {
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
            }
            Platform.request<Boolean>(request) { success ->
                val message = if (success)
                    "Сохранение прошло успешно!"
                else
                    "Возникла ошибка при сохранении :("
                User.request<Unit>(
                    ToUser.PostMessage(
                        message = message,
                        actionName = "Океюшки"
                    )
                ) {
                    reset()
                }
            }
        }
    }

    User.request<Unit>(
        ToUser.PostMessage(
            message = "Рад вас видеть снова :)",
            actionName = "Привет!"
        )
    ) {
        User.request<MainIntent>(
            ToUser.GetChoice(
                title = "Чего пожелаете?",
                items = MainIntent.entries
            )
        ) { item ->
            when (item) {
                MainIntent.AddNote -> {
                    editNote()
                }

                MainIntent.ReadNotes -> {
                    Platform.request<List<Note>>(ToPlatform.GetNotes()) { notes ->
                        User.request<Note>(
                            ToUser.GetChoice<Note>(
                                title = "Заметки",
                                items = notes
                            )
                        ) { note ->
                            editNote(note)
                        }
                    }
                }
            }
        }
    }
}