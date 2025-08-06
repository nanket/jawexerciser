package com.jawexerciser.app.ml

import android.util.Log
import com.jawexerciser.app.data.model.Exercise
import kotlin.math.abs
import kotlin.math.sqrt

class JawExerciseDetector {
    
    companion object {
        private const val TAG = "JawExerciseDetector"
        private const val JAW_OPENER_THRESHOLD = 0.03f
        private const val CHIN_LIFT_THRESHOLD = 0.02f
        private const val SIDE_TO_SIDE_THRESHOLD = 0.025f
        private const val MIN_FRAMES_BETWEEN_REPS = 15
    }
    
    private var currentExerciseType = Exercise.JAW_OPENER_ID
    private var framesSinceLastRep = 0
    private var isInExercisePosition = false
    private var baselineDistance = 0f
    private var baselineFrameCount = 0
    private val baselineFrames = 30 // Frames to establish baseline
    
    // Smoothing variables
    private val distanceHistory = mutableListOf<Float>()
    private val historySize = 5
    
    fun setExerciseType(exerciseType: Int) {
        currentExerciseType = exerciseType
        resetDetector()
    }
    
    private fun resetDetector() {
        framesSinceLastRep = 0
        isInExercisePosition = false
        baselineDistance = 0f
        baselineFrameCount = 0
        distanceHistory.clear()
    }
    
    fun detectExercise(landmarks: FaceLandmarks): ExerciseDetectionResult {
        framesSinceLastRep++
        
        val distance = when (currentExerciseType) {
            Exercise.JAW_OPENER_ID -> calculateJawOpenDistance(landmarks)
            Exercise.CHIN_LIFT_ID -> calculateChinLiftDistance(landmarks)
            Exercise.SIDE_TO_SIDE_ID -> calculateSideToSideDistance(landmarks)
            else -> 0f
        }
        
        // Smooth the distance measurement
        val smoothedDistance = smoothDistance(distance)
        
        // Establish baseline during first frames
        if (baselineFrameCount < baselineFrames) {
            baselineDistance = (baselineDistance * baselineFrameCount + smoothedDistance) / (baselineFrameCount + 1)
            baselineFrameCount++
            return ExerciseDetectionResult(
                repDetected = false,
                currentDistance = smoothedDistance,
                threshold = getThresholdForExercise(),
                isInPosition = false
            )
        }
        
        val threshold = getThresholdForExercise()
        val isCurrentlyInPosition = smoothedDistance > (baselineDistance + threshold)
        
        var repDetected = false
        
        // Detect rep completion (transition from in-position to out-of-position)
        if (isInExercisePosition && !isCurrentlyInPosition && framesSinceLastRep > MIN_FRAMES_BETWEEN_REPS) {
            repDetected = true
            framesSinceLastRep = 0
            Log.d(TAG, "Rep detected for exercise $currentExerciseType")
        }
        
        isInExercisePosition = isCurrentlyInPosition
        
        return ExerciseDetectionResult(
            repDetected = repDetected,
            currentDistance = smoothedDistance,
            threshold = baselineDistance + threshold,
            isInPosition = isCurrentlyInPosition
        )
    }
    
    private fun smoothDistance(newDistance: Float): Float {
        distanceHistory.add(newDistance)
        if (distanceHistory.size > historySize) {
            distanceHistory.removeAt(0)
        }
        return distanceHistory.average().toFloat()
    }
    
    private fun calculateJawOpenDistance(landmarks: FaceLandmarks): Float {
        val upperLip = landmarks.getUpperLipPoint()
        val lowerLip = landmarks.getLowerLipPoint()
        
        return if (upperLip != null && lowerLip != null) {
            calculateDistance(upperLip, lowerLip)
        } else {
            0f
        }
    }
    
    private fun calculateChinLiftDistance(landmarks: FaceLandmarks): Float {
        val chin = landmarks.getChinPoint()
        val noseTip = landmarks.getNoseTip()
        
        return if (chin != null && noseTip != null) {
            // Measure vertical distance change for chin lift
            abs(chin.y - noseTip.y)
        } else {
            0f
        }
    }
    
    private fun calculateSideToSideDistance(landmarks: FaceLandmarks): Float {
        val leftJaw = landmarks.getLeftJawPoint()
        val rightJaw = landmarks.getRightJawPoint()
        val noseTip = landmarks.getNoseTip()
        
        return if (leftJaw != null && rightJaw != null && noseTip != null) {
            // Measure asymmetry in jaw position
            val leftDistance = calculateDistance(leftJaw, noseTip)
            val rightDistance = calculateDistance(rightJaw, noseTip)
            abs(leftDistance - rightDistance)
        } else {
            0f
        }
    }
    
    private fun calculateDistance(point1: FaceLandmark, point2: FaceLandmark): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return sqrt(dx * dx + dy * dy)
    }
    
    private fun getThresholdForExercise(): Float {
        return when (currentExerciseType) {
            Exercise.JAW_OPENER_ID -> JAW_OPENER_THRESHOLD
            Exercise.CHIN_LIFT_ID -> CHIN_LIFT_THRESHOLD
            Exercise.SIDE_TO_SIDE_ID -> SIDE_TO_SIDE_THRESHOLD
            else -> JAW_OPENER_THRESHOLD
        }
    }
}

data class ExerciseDetectionResult(
    val repDetected: Boolean,
    val currentDistance: Float,
    val threshold: Float,
    val isInPosition: Boolean
)
