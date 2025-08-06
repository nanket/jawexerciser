package com.jawexerciser.app.ui.exercise

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.jawexerciser.app.ml.FaceLandmarks

class ExerciseOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val landmarkPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 4f
    }
    
    private val connectionPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val feedbackPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private var faceLandmarks: FaceLandmarks? = null
    private var isInExercisePosition = false
    private var feedbackText = ""
    
    fun updateLandmarks(landmarks: FaceLandmarks?) {
        faceLandmarks = landmarks
        invalidate()
    }
    
    fun updateFeedback(inPosition: Boolean, feedback: String) {
        isInExercisePosition = inPosition
        feedbackText = feedback
        
        // Update landmark color based on position
        landmarkPaint.color = if (inPosition) {
            Color.GREEN
        } else {
            Color.RED
        }
        
        invalidate()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        faceLandmarks?.let { landmarks ->
            drawJawLandmarks(canvas, landmarks)
        }
        
        // Draw feedback text
        if (feedbackText.isNotEmpty()) {
            canvas.drawText(
                feedbackText,
                width / 2f,
                height / 4f,
                feedbackPaint
            )
        }
        
        // Draw exercise position indicator
        drawPositionIndicator(canvas)
    }
    
    private fun drawJawLandmarks(canvas: Canvas, landmarks: FaceLandmarks) {
        val scaleX = width.toFloat()
        val scaleY = height.toFloat()
        
        // Draw key jaw landmarks
        landmarks.getChinPoint()?.let { chin ->
            canvas.drawCircle(
                chin.x * scaleX,
                chin.y * scaleY,
                8f,
                landmarkPaint
            )
        }
        
        landmarks.getUpperLipPoint()?.let { upperLip ->
            canvas.drawCircle(
                upperLip.x * scaleX,
                upperLip.y * scaleY,
                6f,
                landmarkPaint
            )
        }
        
        landmarks.getLowerLipPoint()?.let { lowerLip ->
            canvas.drawCircle(
                lowerLip.x * scaleX,
                lowerLip.y * scaleY,
                6f,
                landmarkPaint
            )
        }
        
        // Draw jaw outline
        val leftJaw = landmarks.getLeftJawPoint()
        val rightJaw = landmarks.getRightJawPoint()
        val chin = landmarks.getChinPoint()
        
        if (leftJaw != null && rightJaw != null && chin != null) {
            val path = Path().apply {
                moveTo(leftJaw.x * scaleX, leftJaw.y * scaleY)
                lineTo(chin.x * scaleX, chin.y * scaleY)
                lineTo(rightJaw.x * scaleX, rightJaw.y * scaleY)
            }
            canvas.drawPath(path, connectionPaint)
        }
    }
    
    private fun drawPositionIndicator(canvas: Canvas) {
        val indicatorRadius = 20f
        val indicatorX = width - 60f
        val indicatorY = 60f
        
        val indicatorPaint = Paint().apply {
            color = if (isInExercisePosition) Color.GREEN else Color.RED
            style = Paint.Style.FILL
        }
        
        canvas.drawCircle(indicatorX, indicatorY, indicatorRadius, indicatorPaint)
    }
}
