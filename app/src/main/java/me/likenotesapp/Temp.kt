package me.likenotesapp

import kotlinx.coroutines.delay
//
//fun main() {
//    // Simple App
//    User.requestAttention("post/screen/splash") {
//        User.requestAttention("post/screen/auth") {
//            User.requestText("get/text/email") { email ->
//                User.requestText("get/text/password") { pass ->
//                    listenUpdates(email, pass) {
//                        val loginEnabled = email.value != null && pass.value != null
//                        User.requestAttention("post/state/login/enabled/$", loginEnabled) {
//                            User.requestChoice("get/choice/login") { loginChoice ->
//                                listenUpdates(loginChoice) {
//                                    User.requestAttention("post/state/login/enabled/$", false) {
//                                        Backend.requestData(
//                                            "post/login",
//                                            email + pass
//                                        ) { loginResult ->
//                                            listenUpdates(loginResult) {
//                                                Platform.requestDataSave(
//                                                    "post/token/$",
//                                                    loginResult.token
//                                                ) { saveToken ->
//                                                    User.requestAttention(
//                                                        "post/login/success/$",
//                                                        loginResult
//                                                    ) {
//                                                        User.requestAttention(if (loginResult) "post/screen/main" else "post/screen/auth") {
//                                                            // TODO:
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun listenUpdates(
//    vararg states: UpdatableState<Any>,
//    onUpdate: () -> Unit
//): UpdatableState<Any> {
//    val updatable = UpdatableState<Any>(Unit)
//    states.forEach {
//        it.listen {
//            onUpdate()
//            updatable.post(Unit)
//        }
//    }
//
//    return updatable
//}
//
//suspend fun main2() {
//    User.requestAttention("post/screen/splash")
//    delay(1000)
//    User.requestAttention("post/screen/auth")
//
//    val email = User.requestText("get/text/email")
//    val pass = User.requestText("get/text/password")
//    listenUpdates(email, pass) {
//        val loginEnabled = email != null && pass != null
//        User.requestAttention("post/state/login/enabled/$", loginEnabled)
//    }
//
//    val loginChoice = User.requestChoice("get/choice/login")
//    listenUpdates(loginChoice) {
//        User.requestAttention("post/state/login/enabled/$", false)
//        val loginResult = Backend.requestData("post/login", email + pass)
//        listenUpdates(loginResult) {
//            Platform.requestDataSave("post/token/$", loginResult.token)
//            User.requestAttention("post/login/success/$", loginResult)
//            User.requestAttention(if (loginResult) "post/screen/main" else "post/screen/auth")
//        }
//    }
//}
//
//suspend fun main3() { // Functional App
//    // На каждый запрос данных открывается экран
//    // на запрос текста - экран с полем ввода
//    // на запрос выбора из списка - экран со списком
//    // на запрос выбора действия(кнопка логин или регистрация) - экран со списком кнопок
//
//    User.requestAttention("post/screen/splash")
//    delay(1000)
//    User.requestAttention("post/screen/auth")
//
//    val email = User.requestText("get/text/email")
//    email.await()
//    val pass = User.requestText("get/text/password")
//    pass.await()
//
//    val loginChoice = User.requestChoice("get/choice/login")
//    loginChoice.await()
//
//    User.requestAttention("post/screen/loading")
//    val loginResult = Backend.requestData("post/login", email + pass)
//    loginResult.await()
//
//    Platform.requestDataSave("post/token/$", loginResult.token)
//
//    User.requestAttention("post/login/success/$", loginResult)
//    delay(1000)
//    User.requestAttention(if (loginResult) "post/screen/main" else "post/screen/auth")
//
//    // NOTE: можно еще отображать каждый пользовательский ввод\выбор строкой,
//// по нажатию на которую программа запускается заново с этой операции, так выбранные данные будут наглядны
//}
//
//fun Backend.requestData(
//    query: String,
//    data: Any = Unit,
//    content: (value: UpdatableState<Any>) -> UpdatableState<Any>
//): UpdatableState<Any> {
//    return content(Unit)
//}
//
//enum class RequestType {
//    Attention,
//    TextInput,
//    Choice,
//}
//
//fun User.requestAttention(
//    query: String,
//    data: Any = Unit,
//    content: (value: UpdatableState<Any>) -> UpdatableState<Any> = { UpdatableState(Unit) }
//): UpdatableState<Any> {
//    return requester.request(RequestType.Attention, query, data, content)
//}
//
//fun User.requestText(
//    query: String,
//    data: Any = Unit,
//    content: (value: UpdatableState<Any>) -> UpdatableState<Any> = { UpdatableState(Unit) }
//): UpdatableState<Any> {
//    return requester.request(RequestType.TextInput, query, data, content)
//}
//
//fun User.requestChoice(
//    query: String,
//    data: Any = Unit,
//    content: (value: UpdatableState<Any>) -> UpdatableState<Any> = { UpdatableState(Unit) }
//): UpdatableState<Any> {
//    return requester.request(RequestType.Choice, query, data, content)
//}