package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcuseDao {
    @Query("SELECT * FROM excuse_history ORDER BY timestamp DESC")
    fun getAllExcuses(): Flow<List<ExcuseEntity>>

    @Query("SELECT * FROM excuse_history WHERE situation LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchExcuses(query: String): Flow<List<ExcuseEntity>>

    @Query("SELECT * FROM excuse_history WHERE id = :id LIMIT 1")
    suspend fun getExcuseById(id: Long): ExcuseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExcuse(excuse: ExcuseEntity): Long

    @Query("DELETE FROM excuse_history WHERE id = :id")
    suspend fun deleteExcuseById(id: Long)

    @Query("DELETE FROM excuse_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM excuse_history")
    fun getCount(): Flow<Int>

    @Query("UPDATE excuse_history SET copiedCount = copiedCount + 1 WHERE id = :id")
    suspend fun incrementCopied(id: Long)
}
