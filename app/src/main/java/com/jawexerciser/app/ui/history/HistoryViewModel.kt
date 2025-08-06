package com.jawexerciser.app.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.jawexerciser.app.data.database.JawExerciserDatabase
import com.jawexerciser.app.data.model.Exercise
import com.jawexerciser.app.data.model.Session
import com.jawexerciser.app.data.repository.ExerciseRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ExerciseRepository
    
    val recentSessions: LiveData<List<Session>>
    val exercises: LiveData<List<Exercise>>
    
    val sessionsWithExercises = MediatorLiveData<List<SessionWithExercise>>()
    
    val totalSessions: LiveData<Int>
    val totalReps: LiveData<Int>
    val totalTimeMinutes: LiveData<Int>
    
    init {
        val database = JawExerciserDatabase.getDatabase(application)
        repository = ExerciseRepository(
            database.exerciseDao(),
            database.sessionDao(),
            database.repDao()
        )
        
        recentSessions = repository.getRecentSessions()
        exercises = repository.getAllExercises()
        
        // Combine sessions with exercise names
        sessionsWithExercises.addSource(recentSessions) { sessions ->
            combineSessionsWithExercises(sessions, exercises.value)
        }
        sessionsWithExercises.addSource(exercises) { exerciseList ->
            combineSessionsWithExercises(recentSessions.value, exerciseList)
        }
        
        // Calculate statistics
        totalSessions = recentSessions.map { it.size }
        
        totalReps = recentSessions.map { sessions ->
            sessions.sumOf { it.totalReps }
        }
        
        totalTimeMinutes = recentSessions.map { sessions ->
            (sessions.sumOf { it.durationSeconds } / 60).toInt()
        }
    }
    
    private fun combineSessionsWithExercises(
        sessions: List<Session>?,
        exerciseList: List<Exercise>?
    ) {
        if (sessions != null && exerciseList != null) {
            val exerciseMap = exerciseList.associateBy { it.id }
            val combined = sessions.map { session ->
                SessionWithExercise(
                    session = session,
                    exerciseName = exerciseMap[session.exerciseId]?.name ?: "Unknown Exercise"
                )
            }
            sessionsWithExercises.value = combined
        }
    }
}
