package io.mvpstarter.sample.com

import android.speech.tts.TextToSpeech
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.*

class NotificationReaderService : NotificationListenerService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("id", "ID")
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName.lowercase()
        val extras = sbn.notification.extras
        
        // Mengambil judul dan teks notifikasi dengan aman
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        // Mengecek apakah notifikasi berasal dari GoBiz, GoPay, atau mengandung kata Gopay
        val isGoPayMerchant = packageName.contains("gojek.merchant") || 
                              packageName.contains("gobiz") || 
                              packageName.contains("gopay") ||
                              title.lowercase().contains("gopay")

        if (isGoPayMerchant && text.isNotEmpty()) {
            // Format suara yang dibacakan
            val pesanSuara = "Ada pembayaran masuk. $text"
            tts?.speak(pesanSuara, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}