package com.example.moneykeepertest

import android.app.NotificationChannel
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_ACCESS_REQUEST_CODE = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiUrlEditText: EditText = findViewById(R.id.api_url)
        val telegramNickEditText: EditText = findViewById(R.id.telegram_nick)
        val sendCachedButton: Button = findViewById(R.id.send_cached)
        val saveSettingsButton: Button = findViewById(R.id.save_settings)
        val requestPermissionButton: Button = findViewById(R.id.request_permission)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        apiUrlEditText.setText(sharedPreferences.getString("api_url", ""))
        telegramNickEditText.setText(sharedPreferences.getString("telegram_nick", ""))

        saveSettingsButton.setOnClickListener {
            val apiUrl = apiUrlEditText.text.toString()
            val telegramNick = telegramNickEditText.text.toString()

            val editor = sharedPreferences.edit()
            editor.putString("api_url", apiUrl)
            editor.putString("telegram_nick", telegramNick)
            editor.apply()
        }


        sendCachedButton.setOnClickListener {
            val apiUrl = apiUrlEditText.text.toString()
            val telegramNick = telegramNickEditText.text.toString()
            val notificationManager = NotificationManager(this, apiUrl, telegramNick)
            notificationManager.sendCachedNotifications()
        }

        requestPermissionButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivityForResult(intent, NOTIFICATION_ACCESS_REQUEST_CODE)
        }

        checkNotificationAccessPermission()
    }



    private fun checkNotificationAccessPermission() {
        val sets = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val packageName = packageName

        if (sets == null || !sets.contains(packageName)) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivityForResult(intent, NOTIFICATION_ACCESS_REQUEST_CODE)
        }
    }
}
