package com.jawexerciser.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val previewView: PreviewView,
    private val lifecycleOwner: LifecycleOwner
) {
    
    companion object {
        private const val TAG = "CameraManager"
    }
    
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    
    interface FrameAnalysisListener {
        fun onFrameAnalyzed(bitmap: Bitmap)
    }
    
    private var frameAnalysisListener: FrameAnalysisListener? = null
    
    fun setFrameAnalysisListener(listener: FrameAnalysisListener) {
        frameAnalysisListener = listener
    }
    
    fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Log.e(TAG, "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        
        // Preview use case
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        
        // Image analysis use case
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor, ImageAnalyzer())
            }
        
        // Camera selector for front camera
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        
        try {
            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()
            
            // Bind use cases to camera
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            
            Log.d(TAG, "Camera bound successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }
    
    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            try {
                val bitmap = imageProxyToBitmap(image)
                bitmap?.let { frameAnalysisListener?.onFrameAnalyzed(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing image", e)
            } finally {
                image.close()
            }
        }
    }
    
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        return try {
            // Create a simple placeholder bitmap for development
            // In production, implement proper YUV to RGB conversion
            val width = 640
            val height = 480
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(android.graphics.Color.GRAY)
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error converting ImageProxy to Bitmap", e)
            null
        }
    }
    
    private fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            preScale(-1f, 1f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }
    
    fun stopCamera() {
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }
}
