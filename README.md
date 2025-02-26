### Приложение "Блокнот" 
## и "Архитектура построенная на запросах"

Привет :)

Я обратил внимание что все операции которые делает приложение - это по сути запросы данных от разных источников. 

Будь то машина или человек. 

Приложение принимает данные от какого-то источника, преобразует их и выдает преобразованные данные какому-то источнику. 

Решил сделать экспериментальное приложение и вот что из этого получилось :)

<details>

<summary>
Вся бизнес-логика сводится к такому дерево-графу запросов (к которому можно прикрутить любую платфоменную и UI реализацию)
</summary>

```kotlin
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

fun openTrash(removedNotes: List<Note>) {
  // ...
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
    // ...
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
```
</details>

### ✅ Стек:

Android, Kotlin, Compose, Coroutines, Room

### ✅ Цели:
- __Сделать приложение на Compose__
- Попрактиковаться в создании приложений на Compose
- Написать приложение в стиле запрос-ответов
- Написать приложение максимально просто
- Это должно быть полноценное, функциональное приложение
- Использовать код в качестве демонстрации, как домашнее тестовое задание

### ✅ Дополнительно удалось:
- отказаться от использования suspend функций (они используются только там где действительно нужны)
- использовать минимум фреймворков (без фреймворков DI, Flow, навигации, вьюмоделей)

### ✅ Вводные для читателя кода:
Точка входа: __main()__
- __handleRequestsToPlatform()__ - обработка запросов к платформе
- __handleRequestsToUser()__ - обработка запросов к пользователю
- __Клиенты__ - делаем к ним запросы и получаем от них ответы
  - __User__ - пользователь как клиент
  - __Platform__ - платформа/OS как клиент
  - __Backend__ - бэк как клиент
- __ToUser__ - запросы к пользователю
  - __PostMessage__ - передать сообщение пользователю
  - __GetChoice__ - получить выбор из вариантов
  - __GetString__ - получить текст

- __ToPlatform__ - запросы к платформе/OS
  - __...__ 

Вся работа корутин производится в едином скоупе приложения.

Данные сохраняются в БД с помощью Room.
 
<hr />

🏁 Если есть вопросы и предложения то [напиши мне в телеграм](https://t.me/alex_ku_san) 🏁
