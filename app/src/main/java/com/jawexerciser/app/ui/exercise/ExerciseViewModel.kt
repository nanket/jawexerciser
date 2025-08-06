package com.jawexerciser.app.ui.exercise

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jawexerciser.app.data.database.JawExerciserDatabase
import com.jawexerciser.app.data.model.Rep
import com.jawexerciser.app.data.model.Session
import com.jawexerciser.app.data.repository.ExerciseRepository
import com.jawexerciser.app.ml.ExerciseDetectionResult
import com.jawexerciser.app.ml.FaceLandmarkDetector
import com.jawexerciser.app.ml.FaceLandmarks
import com.jawexerciser.app.ml.JawExerciseDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ExerciseRepository
    private val faceLandmarkDetector = FaceLandmarkDetector(application)
    private val jawExerciseDetector = JawExerciseDetector()
    
    private var currentSessionId: Long = 0
    private var sessionStartTime = Date()
    
    private val _repCount = MutableLiveData(0)
    val repCount: LiveData<Int> = _repCount
    
    private val _duration = MutableLiveData(0L)
    val duration: LiveData<Long> = _duration
    
    private val _status = MutableLiveData("Initializing...")
    val status: LiveData<String> = _status
    
    private val _faceLandmarks = MutableLiveData<FaceLandmarks?>()
    val faceLandmarks: LiveData<FaceLandmarks?> = _faceLandmarks
    
    private val _isInPosition = MutableLiveData(false)
    val isInPosition: LiveData<Boolean> = _isInPosition
    
    private val _sessionComplete = MutableLiveData<Session?>()
    val sessionComplete: LiveData<Session?> = _sessionComplete
    
    init {
        val database = JawExerciserDatabase.getDatabase(application)
        repository = ExerciseRepository(
            database.exerciseDao(),
            database.sessionDao(),
            database.repDao()
        )
        
        viewModelScope.launch {
            if (faceLandmarkDetector.initialize()) {
                _status.value = "Ready"
            } else {
                _status.value = "Camera Error"
            }
        }
    }
    
    fun startSession(exerciseId: Int) {
        viewModelScope.launch {
            sessionStartTime = Date()
            jawExerciseDetector.setExerciseType(exerciseId)
            
            val session = Session(
                exerciseId = exerciseId,
                startTime = sessionStartTime,
                endTime = null
            )
            
            currentSessionId = repository.insertSession(session)
            _status.value = "Session Started"
        }
    }
    
    fun processFrame(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val landmarks = faceLandmarkDetector.detectLandmarks(bitmap)
                _faceLandmarks.postValue(landmarks)
                
                landmarks?.let { 
                    val result = jawExerciseDetector.detectExercise(it)
                    handleDetectionResult(result)
                }
            } catch (e: Exception) {
                _status.postValue("Processing Error")
            }
        }
    }
    
    private suspend fun handleDetectionResult(result: ExerciseDetectionResult) {
        _isInPosition.postValue(result.isInPosition)
        
        if (result.repDetected) {
            val newRepCount = _repCount.value!! + 1
            _repCount.postValue(newRepCount)
            
            // Save rep to database
            val rep = Rep(
                sessionId = currentSessionId,
                timestamp = Date(),
                quality = 1.0f // Could be calculated based on detection quality
            )
            repository.insertRep(rep)
            
            _status.postValue("Rep $newRepCount completed!")
        } else {
            _status.postValue(
                if (result.isInPosition) "Hold position" else "Ready for next rep"
            )
        }
    }
    
    fun endSession() {
        viewModelScope.launch {
            val endTime = Date()
            val durationSeconds = (endTime.time - sessionStartTime.time) / 1000
            
            val session = repository.getSessionById(currentSessionId)
            session?.let { currentSession ->
                val updatedSession = currentSession.copy(
                    endTime = endTime,
                    totalReps = _repCount.value ?: 0,
                    durationSeconds = durationSeconds
                )
                repository.updateSession(updatedSession)
                _sessionComplete.postValue(updatedSession)
            }
        }
    }
    
    fun updateDuration(seconds: Long) {
        _duration.value = seconds
    }
    
    override fun onCleared() {
        super.onCleared()
        faceLandmarkDetector.close()
    }
}
