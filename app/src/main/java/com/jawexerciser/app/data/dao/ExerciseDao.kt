package com.jawexerciser.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jawexerciser.app.data.model.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY id")
    fun getAllExercises(): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Int): Exercise?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)
    
    @Update
    suspend fun updateExercise(exercise: Exercise)
    
    @Delete
    suspend fun deleteExercise(exercise: Exercise)
}
