package com.nenolink.gmailcatch

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationManagerCompat

class MainActivity : Activity() {
    private lateinit var settingsRepo: SettingsRepository
    private lateinit var status: TextView
    private lateinit var sender: EditText
    private lateinit var subjectPrefix: EditText
    private lateinit var subjectRequired: CheckBox
    private lateinit var enabled: Switch
    private lateinit var log: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepo = SettingsRepository(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }
        val title = TextView(this).apply {
            text = "Gmail Catch"
            textSize = 30f
            setTypeface(typeface, Typeface.BOLD)
        }
        val intro = TextView(this).apply {
            text = "Alarm ved Gmail-notifikation fra en bestemt VIP-afsender. Alt behandles lokalt på telefonen."
            textSize = 16f
        }
        enabled = Switch(this).apply {
            text = "Mailalarm aktiv"
            isChecked = settingsRepo.enabled
            setOnCheckedChangeListener { _, checked -> settingsRepo.enabled = checked }
        }
        sender = EditText(this).apply {
            hint = "Afsender, fx noreply@em.lilt.com"
            setText(settingsRepo.vipSender)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        subjectRequired = CheckBox(this).apply {
            text = "Kræv emne-prefix"
            isChecked = settingsRepo.subjectRequired
        }
        subjectPrefix = EditText(this).apply {
            hint = "Emnet begynder med"
            setText(settingsRepo.subjectPrefix)
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        val save = Button(this).apply {
            text = "GEM MAILREGEL"
            setOnClickListener {
                settingsRepo.vipSender = sender.text.toString()
                settingsRepo.subjectPrefix = subjectPrefix.text.toString()
                settingsRepo.subjectRequired = subjectRequired.isChecked
                refreshStatus()
                Toast.makeText(this@MainActivity, "Mailregel gemt", Toast.LENGTH_SHORT).show()
            }
        }
        val notificationAccess = Button(this).apply {
            text = "GIV NOTIFIKATIONSADGANG"
            setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        }
        val fullScreen = Button(this).apply {
            text = "KONTROLLÉR FULL-SCREEN ALARM"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= 34) {
                    startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                        data = android.net.Uri.parse("package:$packageName")
                    })
                } else {
                    Toast.makeText(this@MainActivity, "Ikke nødvendig på denne Android-version", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val test = Button(this).apply {
            text = "TEST ALARM"
            setOnClickListener {
                requestNotificationPermissionIfNeeded()
                startForegroundService(Intent(this@MainActivity, AlarmService::class.java).apply {
                    action = AlarmService.ACTION_START
                    putExtra(AlarmService.EXTRA_REASON, "Testalarm")
                })
            }
        }
        status = TextView(this).apply { textSize = 16f }
        val logTitle = TextView(this).apply {
            text = "Diagnostik"
            textSize = 20f
            setTypeface(typeface, Typeface.BOLD)
        }
        log = TextView(this).apply { textSize = 13f }

        listOf(title, intro, enabled, sender, subjectRequired, subjectPrefix, save, notificationAccess, fullScreen, test, status, logTitle, log).forEach { view ->
            root.addView(view, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = if (view === title) 0 else 20
            })
        }
        setContentView(ScrollView(this).apply { addView(root) })
        requestNotificationPermissionIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        val listenerEnabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
        val fullScreenAllowed = if (Build.VERSION.SDK_INT >= 34) {
            getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
        } else true
        status.text = buildString {
            append("Status\n")
            append(if (listenerEnabled) "✓ Notifikationsadgang\n" else "✗ Notifikationsadgang mangler\n")
            append(if (fullScreenAllowed) "✓ Full-screen alarm tilladt\n" else "✗ Full-screen alarm kræver adgang\n")
            append(if (settingsRepo.vipSender.isBlank()) "✗ Afsender mangler\n" else "✓ Afsender konfigureret\n")
            append(if (settingsRepo.subjectRequired && settingsRepo.subjectPrefix.isBlank()) "✗ Emne-prefix mangler" else "✓ Emneregel konfigureret")
        }
        log.text = EventLog.read(this)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        }
    }
}
