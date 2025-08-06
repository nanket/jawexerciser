package com.jawexerciser.app.ui.exercise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jawexerciser.app.R
import com.jawexerciser.app.camera.CameraManager
import com.jawexerciser.app.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_EXERCISE_ID = "exercise_id"
        const val EXTRA_EXERCISE_NAME = "exercise_name"
    }
    
    private lateinit var binding: ActivityExerciseBinding
    private val viewModel: ExerciseViewModel by viewModels()
    private lateinit var cameraManager: CameraManager
    
    private var exerciseId: Int = 1
    private var exerciseName: String = ""
    private var sessionStartTime: Long = 0
    private val durationHandler = Handler(Looper.getMainLooper())
    private val durationRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val durationSeconds = (currentTime - sessionStartTime) / 1000
            viewModel.updateDuration(durationSeconds)
            durationHandler.postDelayed(this, 1000)
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        extractIntentData()
        setupUI()
        setupObservers()
        checkCameraPermission()
    }
    
    private fun extractIntentData() {
        exerciseId = intent.getIntExtra(EXTRA_EXERCISE_ID, 1)
        exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME) ?: "Exercise"
    }
    
    private fun setupUI() {
        binding.exerciseNameText.text = exerciseName
        binding.instructionText.text = getInstructionForExercise(exerciseId)
        binding.repCountText.text = "0"
        binding.durationText.text = "00:00"
        binding.statusText.text = "Ready"
        
        binding.endSessionButton.setOnClickListener {
            endSession()
        }
    }
    
    private fun getInstructionForExercise(exerciseId: Int): String {
        return when (exerciseId) {
            1 -> getString(R.string.exercise_description_jaw_opener)
            2 -> getString(R.string.exercise_description_chin_lift)
            3 -> getString(R.string.exercise_description_side_to_side)
            else -> "Follow the exercise instructions"
        }
    }
    
    private fun setupObservers() {
        viewModel.repCount.observe(this) { count ->
            binding.repCountText.text = count.toString()
        }
        
        viewModel.duration.observe(this) { seconds ->
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            binding.durationText.text = String.format("%02d:%02d", minutes, remainingSeconds)
        }
        
        viewModel.status.observe(this) { status ->
            binding.statusText.text = status
        }
        
        viewModel.faceLandmarks.observe(this) { landmarks ->
            binding.overlayView.updateLandmarks(landmarks)
        }
        
        viewModel.isInPosition.observe(this) { inPosition ->
            val feedback = if (inPosition) "Good position!" else "Adjust position"
            binding.overlayView.updateFeedback(inPosition, feedback)
        }
        
        viewModel.sessionComplete.observe(this) { session ->
            session?.let {
                showSessionSummary(it.totalReps, it.durationSeconds)
            }
        }
    }
    
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun startCamera() {
        cameraManager = CameraManager(this, binding.previewView, this)
        cameraManager.setFrameAnalysisListener(object : CameraManager.FrameAnalysisListener {
            override fun onFrameAnalyzed(bitmap: android.graphics.Bitmap) {
                viewModel.processFrame(bitmap)
            }
        })
        cameraManager.startCamera()
        
        // Start session and timer
        viewModel.startSession(exerciseId)
        sessionStartTime = System.currentTimeMillis()
        durationHandler.post(durationRunnable)
    }
    
    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Camera Permission Required")
            .setMessage(getString(R.string.camera_permission_required))
            .setPositiveButton(getString(R.string.grant_permission)) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()
    }
    
    private fun endSession() {
        durationHandler.removeCallbacks(durationRunnable)
        viewModel.endSession()
    }
    
    private fun showSessionSummary(reps: Int, durationSeconds: Long) {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        val durationText = String.format("%02d:%02d", minutes, seconds)
        
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.session_summary))
            .setMessage("Exercise: $exerciseName\nReps: $reps\nDuration: $durationText")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        durationHandler.removeCallbacks(durationRunnable)
        if (::cameraManager.isInitialized) {
            cameraManager.stopCamera()
        }
    }
}
