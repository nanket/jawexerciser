package com.jawexerciser.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.jawexerciser.app.data.database.JawExerciserDatabase
import com.jawexerciser.app.data.model.Exercise
import com.jawexerciser.app.data.repository.ExerciseRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ExerciseRepository
    val exercises: LiveData<List<Exercise>>
    
    init {
        val database = JawExerciserDatabase.getDatabase(application)
        repository = ExerciseRepository(
            database.exerciseDao(),
            database.sessionDao(),
            database.repDao()
        )
        exercises = repository.getAllExercises()
    }
}
