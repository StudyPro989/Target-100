package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT value FROM app_settings WHERE key = :key")
    suspend fun getSettingValue(key: String): String?

    @Query("SELECT value FROM app_settings WHERE key = :key")
    fun getSettingFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingsEntity)

    @Query("SELECT * FROM app_settings")
    suspend fun getAllSettings(): List<AppSettingsEntity>

    @Query("DELETE FROM app_settings WHERE key = :key")
    suspend fun deleteSetting(key: String)
}
