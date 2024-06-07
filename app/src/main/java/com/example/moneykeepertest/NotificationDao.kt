package com.example.moneykeepertest

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications")
    fun getAll(): LiveData<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    fun deleteAll()

    @Delete
    fun delete(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM notifications")
    fun getAllCachedNotifications(): List<NotificationEntity>
}
