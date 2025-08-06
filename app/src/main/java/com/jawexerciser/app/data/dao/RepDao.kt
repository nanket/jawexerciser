package com.jawexerciser.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jawexerciser.app.data.model.Rep

@Dao
interface RepDao {
    @Query("SELECT * FROM reps WHERE sessionId = :sessionId ORDER BY timestamp")
    fun getRepsBySession(sessionId: Long): LiveData<List<Rep>>
    
    @Query("SELECT COUNT(*) FROM reps WHERE sessionId = :sessionId")
    suspend fun getRepCountBySession(sessionId: Long): Int
    
    @Insert
    suspend fun insertRep(rep: Rep): Long
    
    @Insert
    suspend fun insertReps(reps: List<Rep>)
    
    @Update
    suspend fun updateRep(rep: Rep)
    
    @Delete
    suspend fun deleteRep(rep: Rep)
    
    @Query("DELETE FROM reps WHERE sessionId = :sessionId")
    suspend fun deleteRepsBySession(sessionId: Long)
}
