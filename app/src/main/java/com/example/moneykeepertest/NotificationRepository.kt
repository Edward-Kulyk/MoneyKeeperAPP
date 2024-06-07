package com.example.moneykeepertest

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(context: Context) {

    private val notificationDao: NotificationDao
    private val database: NotificationDatabase

    init {
        database = Room.databaseBuilder(context, NotificationDatabase::class.java, "notifications").build()
        notificationDao = database.notificationDao()
    }


    suspend fun insert(notification: NotificationEntity) {
        withContext(Dispatchers.IO) {
            notificationDao.insert(notification)
        }
    }


    suspend fun delete(notification: NotificationEntity) {
        withContext(Dispatchers.IO) {
            notificationDao.delete(notification)
        }
    }


    suspend fun getAllCachedNotifications(): List<NotificationEntity> {
        return withContext(Dispatchers.IO) {
            notificationDao.getAllCachedNotifications()
        }
    }

}
