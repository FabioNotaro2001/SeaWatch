package com.example.seawatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.example.seawatch.data.uploadToServer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*

class BackgroundService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel"
    private val NOTIFICATION_ID = 1

    private val INTERVAL = 60 * 1000L // 1 minute interval
    private var timer: Timer? = null
    private lateinit var coroutineScope: CoroutineScope

    val avvistamentiViewModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(AvvistamentiViewModel::class.java) }
    val avvistamentiViewViewModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(AvvistamentiViewViewModel::class.java) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("KEYYY", "partito")

        // Creazione della notifica per il servizio foreground
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Caricamento avvistamenti in corso...")
            .setSmallIcon(R.drawable.sea)
            .setContentIntent(pendingIntent)
            .build()

        // Avvio del servizio in modalità foreground
        startForeground(NOTIFICATION_ID, notification)

        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        startTimer()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        // Annullamento del CoroutineScope alla distruzione del servizio
        coroutineScope.cancel()
        stopTimer()
        super.onDestroy()
    }

    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                coroutineScope.launch {
                    Log.e("KEYYY", "giro+")
                    val avvistamenti = runBlocking { avvistamentiViewModel.all.first() }
                    // Passa gli avvistamenti alla funzione di upload
                    uploadToServer(applicationContext, avvistamenti, avvistamentiViewViewModel, avvistamentiViewModel)
                }
            }
        }, 0, INTERVAL)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    // Creazione del canale di notifica per il servizio foreground
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
