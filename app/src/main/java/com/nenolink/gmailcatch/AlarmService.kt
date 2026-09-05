package com.nenolink.gmailcatch

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AlarmService : Service() {
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAlarm()
            return START_NOT_STICKY
        }

        val reason = intent?.getStringExtra(EXTRA_REASON) ?: "VIP-mail modtaget"
        startForeground(NOTIFICATION_ID, buildNotification(reason))
        startSound()
        startVibration()
        EventLog.add(this, "Alarm startet")
        return START_NOT_STICKY
    }

    private fun buildNotification(reason: String): Notification {
        val alarmIntent = Intent(this, AlarmActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        val fullScreen = PendingIntent.getActivity(
            this, 1, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 2,
            Intent(this, AlarmService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Gmail Catch — ALARM")
            .setContentText(reason)
            .setCategory(Notification.CATEGORY_ALARM)
            .setOngoing(true)
            .setFullScreenIntent(fullScreen, true)
            .setContentIntent(fullScreen)
            .addAction(Notification.Action.Builder(null, "STOP", stopIntent).build())
            .build()
    }

    private fun startSound() {
        if (player != null) return
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(this@AlarmService, uri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 700, 400, 700, 400)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 1))
    }

    private fun stopAlarm() {
        player?.runCatching { stop() }
        player?.release()
        player = null
        vibrator?.cancel()
        EventLog.add(this, "Alarm stoppet")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        player?.release()
        vibrator?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "VIP-mailalarmer",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Vedvarende alarmer for matchede Gmail VIP-afsendere"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_START = "com.nenolink.gmailcatch.START_ALARM"
        const val ACTION_STOP = "com.nenolink.gmailcatch.STOP_ALARM"
        const val EXTRA_REASON = "reason"
        private const val CHANNEL_ID = "vip_alarm_v1"
        private const val NOTIFICATION_ID = 1001
    }
}
