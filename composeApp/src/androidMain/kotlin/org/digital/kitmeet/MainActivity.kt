package org.digital.kitmeet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.digital.settings.AndroidContextWrapper
import com.digital.supabaseclients.SupabaseManager
import com.google.firebase.FirebaseApp
import org.digital.kitmeet.notifications.BaseFcmHandler
import org.digital.kitmeet.notifications.FCMTokenProvider
import org.digital.kitmeet.notifications.FcmDelegate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContextWrapper.init(this)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

//class MyApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        FirebaseApp.initializeApp(this)
//
//        val handler = BaseFcmHandler(SupabaseManager.supabaseClient)
//        FcmDelegate.handler = handler
//
//        handler.addTokenListener { token ->
//            viewModelScope.launch {
//                handler.registerToken(currentUserId, token)
//            }
//        }
//    }
//}

//class BaseFcmHandler(
//    private val supabaseClient: SupabaseClient
//) : FcmHandler {
//    private val tokenListeners = mutableListOf<(String) -> Unit>()
//
//    override fun onNewToken(token: String) {
//        tokenListeners.forEach { it(token) }
//    }
//
//    override fun onMessageReceived(data: Map<String, String>) {
//        // Обработка входящих сообщений
//        val title = data["title"] ?: "New message"
//        val body = data["body"] ?: ""
//        println("Received FCM message: $title - $body")
//    }
//
//    fun addTokenListener(listener: (String) -> Unit) {
//        tokenListeners.add(listener)
//    }
//
//    suspend fun registerToken(userId: String, token: String) {
//        supabaseClient.from("user_tokens").upsert(
//            mapOf(
//                "user_id" to userId,
//                "token" to token,
//                "platform" to Platform.osFamily.name,
//                "updated_at" to Clock.System.now().toString()
//            )
//        )
//    }
//}