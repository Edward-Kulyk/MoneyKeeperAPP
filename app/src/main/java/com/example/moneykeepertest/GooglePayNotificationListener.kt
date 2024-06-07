package com.example.moneykeepertest

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class GooglePayNotificationListener : NotificationListenerService() {

    @SuppressLint("SuspiciousIndentation")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification: Notification = sbn?.notification ?: return
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE)
        val text = extras.getString(Notification.EXTRA_TEXT)
        val packageName = sbn.packageName
        val packageManager: PackageManager = packageManager
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        if (appName == "Google Play services") {
            if (text != null) {
                if (title != null) {
                    sendToServer(title = title, text = text, packageName, packageManager)
                }
            }
        }
    }

    private fun sendToServer(title: String, text: String,packageName:String,packageManager:PackageManager) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val apiUrl = sharedPreferences.getString("api_url", "") ?: ""
        val telegramNick = sharedPreferences.getString("telegram_nick", "") ?: ""
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        if (apiUrl.isEmpty() || telegramNick.isEmpty()) {
            cacheNotification(title, text,appName)
            return
        }

        val json = JSONObject()
        json.put("title", title)
        json.put("text", text)
        json.put("telegramNick", telegramNick)
        json.put("appName",appName)

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cacheNotification(title, text,appName)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    cacheNotification(title, text,appName)
                }
            }
        })
    }

    private fun cacheNotification(title: String, text: String,appName:String) {
        val repository = NotificationRepository(this)
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(NotificationEntity(title = title, text = text,appName = appName))
        }
    }
}
