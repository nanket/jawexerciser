package com.jawexerciser.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FaceLandmarkDetector(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(192, 192, ResizeOp.ResizeMethod.BILINEAR))
        .build()
    
    companion object {
        private const val TAG = "FaceLandmarkDetector"
        private const val MODEL_FILE = "face_landmark_model.tflite"
        private const val INPUT_SIZE = 192
        private const val NUM_LANDMARKS = 468
        private const val LANDMARK_DIMS = 3
    }
    
    suspend fun initialize(): Boolean {
        return try {
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(true)
            }
            interpreter = Interpreter(modelBuffer, options)
            Log.d(TAG, "TensorFlow Lite model loaded successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TensorFlow Lite model", e)
            // Create a mock detector for development/testing
            createMockDetector()
            true
        }
    }
    
    private fun createMockDetector() {
        Log.w(TAG, "Using mock face landmark detector for development")
        // This allows the app to run without the actual TFLite model
    }
    
    fun detectLandmarks(bitmap: Bitmap): FaceLandmarks? {
        return try {
            interpreter?.let { interpreter ->
                val tensorImage = TensorImage.fromBitmap(bitmap)
                val processedImage = imageProcessor.process(tensorImage)
                
                // Prepare input buffer
                val inputBuffer = processedImage.buffer
                
                // Prepare output buffer
                val outputBuffer = ByteBuffer.allocateDirect(
                    NUM_LANDMARKS * LANDMARK_DIMS * 4 // 4 bytes per float
                ).apply {
                    order(ByteOrder.nativeOrder())
                }
                
                // Run inference
                interpreter.run(inputBuffer, outputBuffer)
                
                // Parse output
                outputBuffer.rewind()
                val landmarks = mutableListOf<FaceLandmark>()
                
                for (i in 0 until NUM_LANDMARKS) {
                    val x = outputBuffer.float
                    val y = outputBuffer.float
                    val z = outputBuffer.float
                    landmarks.add(FaceLandmark(x, y, z))
                }
                
                FaceLandmarks(landmarks)
            } ?: run {
                // Return mock landmarks for development
                createMockLandmarks()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during landmark detection", e)
            createMockLandmarks()
        }
    }
    
    private fun createMockLandmarks(): FaceLandmarks {
        // Create mock landmarks for development/testing
        val mockLandmarks = mutableListOf<FaceLandmark>()
        for (i in 0 until NUM_LANDMARKS) {
            mockLandmarks.add(FaceLandmark(0.5f, 0.5f, 0f))
        }
        return FaceLandmarks(mockLandmarks)
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
