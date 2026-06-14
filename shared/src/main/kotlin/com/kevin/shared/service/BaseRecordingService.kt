package com.kevin.shared.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class BaseRecordingService : Service() {

    protected val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    abstract val notificationChannelId: String
    abstract val notificationChannelName: String
    abstract val notificationId: Int

    // Override with ServiceInfo.FOREGROUND_SERVICE_TYPE_* constant; 0 = no explicit type
    open val fgsType: Int = 0

    abstract fun buildNotification(label: String, elapsed: String): Notification

    // Called inside a coroutine on serviceScope — subclass can launch additional children
    abstract suspend fun onRecordingStart(label: String)

    // suspend so stopSelf() waits until this completes before service is destroyed
    abstract suspend fun onRecordingStop()

    override fun onCreate() {
        super.onCreate()
        NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_LOW)
            .also { getSystemService(NotificationManager::class.java).createNotificationChannel(it) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            RecordingServiceContract.ACTION_START -> {
                val label = intent.getStringExtra(RecordingServiceContract.EXTRA_LABEL) ?: "Training"
                val notification = buildNotification(label, "00:00")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && fgsType != 0) {
                    startForeground(notificationId, notification, fgsType)
                } else {
                    startForeground(notificationId, notification)
                }
                serviceScope.launch { onRecordingStart(label) }
            }
            RecordingServiceContract.ACTION_STOP -> {
                serviceScope.launch {
                    onRecordingStop()
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    protected fun updateNotification(label: String, elapsed: String) {
        getSystemService(NotificationManager::class.java)
            .notify(notificationId, buildNotification(label, elapsed))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
