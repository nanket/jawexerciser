package com.jawexerciser.app.data.repository

import androidx.lifecycle.LiveData
import com.jawexerciser.app.data.dao.ExerciseDao
import com.jawexerciser.app.data.dao.RepDao
import com.jawexerciser.app.data.dao.SessionDao
import com.jawexerciser.app.data.model.Exercise
import com.jawexerciser.app.data.model.Rep
import com.jawexerciser.app.data.model.Session
import java.util.Calendar
import java.util.Date

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val sessionDao: SessionDao,
    private val repDao: RepDao
) {
    
    // Exercise operations
    fun getAllExercises(): LiveData<List<Exercise>> = exerciseDao.getAllExercises()
    
    suspend fun getExerciseById(id: Int): Exercise? = exerciseDao.getExerciseById(id)
    
    // Session operations
    fun getAllSessions(): LiveData<List<Session>> = sessionDao.getAllSessions()
    
    fun getRecentSessions(): LiveData<List<Session>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        return sessionDao.getRecentSessions(calendar.time)
    }
    
    fun getSessionsByExercise(exerciseId: Int): LiveData<List<Session>> = 
        sessionDao.getSessionsByExercise(exerciseId)
    
    suspend fun insertSession(session: Session): Long = sessionDao.insertSession(session)
    
    suspend fun updateSession(session: Session) = sessionDao.updateSession(session)
    
    suspend fun getSessionById(id: Long): Session? = sessionDao.getSessionById(id)
    
    // Rep operations
    fun getRepsBySession(sessionId: Long): LiveData<List<Rep>> = repDao.getRepsBySession(sessionId)
    
    suspend fun insertRep(rep: Rep): Long = repDao.insertRep(rep)
    
    suspend fun getRepCountBySession(sessionId: Long): Int = repDao.getRepCountBySession(sessionId)
}
