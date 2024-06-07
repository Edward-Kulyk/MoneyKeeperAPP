package com.example.moneykeepertest

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class NotificationManager(private val context: Context, private val apiUrl: String, private val telegramNick: String) {

    private val repository: NotificationRepository = NotificationRepository(context)

    fun sendNotification(notification: NotificationEntity) {
        val json = JSONObject().apply {
            put("title", notification.title)
            put("text", notification.text)
            put("telegramNick", telegramNick)
            put("appName", notification.appName)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NotificationManager", "Failed to send notification", e)
                // Логика обработки неудачной отправки, если необходимо
                println("Failed to send notification: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("NotificationManager", "Notification sent successfully")
                    // Успешная отправка, удаляем уведомление из базы данных
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.delete(notification)
                    }
                } else {
                    Log.e("NotificationManager", "Failed to send notification, response code: ${response.code}")
                    // Логика обработки неудачной отправки
                    println("Failed to send notification, response code: ${response.code}")
                }
            }
        })
    }


    fun sendCachedNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            val cachedNotifications = repository.getAllCachedNotifications()
            cachedNotifications.forEach { notification ->
                sendNotification(notification)
            }
        }
    }

}
