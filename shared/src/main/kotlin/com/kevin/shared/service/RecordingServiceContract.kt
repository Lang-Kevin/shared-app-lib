package com.kevin.shared.service

import android.content.Context
import android.content.Intent

object RecordingServiceContract {
    const val ACTION_START = "com.kevin.shared.ACTION_START"
    const val ACTION_STOP  = "com.kevin.shared.ACTION_STOP"
    const val EXTRA_LABEL  = "label"

    inline fun <reified T : BaseRecordingService> startIntent(context: Context, label: String) =
        Intent(context, T::class.java).apply {
            action = ACTION_START
            putExtra(EXTRA_LABEL, label)
        }

    inline fun <reified T : BaseRecordingService> stopIntent(context: Context) =
        Intent(context, T::class.java).apply { action = ACTION_STOP }
}
