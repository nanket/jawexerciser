package com.jawexerciser.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jawexerciser.app.data.model.Session
import java.util.Date

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): LiveData<List<Session>>
    
    @Query("SELECT * FROM sessions WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    fun getSessionsInDateRange(startDate: Date, endDate: Date): LiveData<List<Session>>
    
    @Query("SELECT * FROM sessions WHERE exerciseId = :exerciseId ORDER BY startTime DESC")
    fun getSessionsByExercise(exerciseId: Int): LiveData<List<Session>>
    
    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): Session?
    
    @Insert
    suspend fun insertSession(session: Session): Long
    
    @Update
    suspend fun updateSession(session: Session)
    
    @Delete
    suspend fun deleteSession(session: Session)
    
    @Query("SELECT * FROM sessions WHERE startTime >= :sevenDaysAgo ORDER BY startTime DESC")
    fun getRecentSessions(sevenDaysAgo: Date): LiveData<List<Session>>
}
