package org.digital.kitmeet.notifications

interface FcmHandler {
    fun onNewToken(token: String)
    fun onMessageReceived(data: Map<String, String>)
}

object FcmDelegate {
    var handler: FcmHandler? = null
}

//expect class MessageSender {
//    fun sendMessage(content : String, conversationId : String, fcmId : String)
//}