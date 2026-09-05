package com.nenolink.gmailcatch

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class AlarmActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val title = TextView(this).apply {
            text = "VIP-MAIL"
            textSize = 36f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "Gmail Catch har registreret en mail fra din VIP-afsender."
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        val stop = Button(this).apply {
            text = "STOP ALARM"
            textSize = 22f
            setOnClickListener {
                startService(Intent(this@AlarmActivity, AlarmService::class.java).setAction(AlarmService.ACTION_STOP))
                finish()
            }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.rgb(120, 0, 0))
            addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(subtitle, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 32 })
            addView(stop, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 56 })
        })
    }
}
