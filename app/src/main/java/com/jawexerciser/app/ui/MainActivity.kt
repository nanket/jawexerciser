package com.jawexerciser.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jawexerciser.app.databinding.ActivityMainBinding
import com.jawexerciser.app.ui.adapter.ExerciseAdapter
import com.jawexerciser.app.ui.exercise.ExerciseActivity
import com.jawexerciser.app.ui.history.HistoryActivity
import com.jawexerciser.app.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate started")

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setupToolbar()
            setupRecyclerView()
            setupFab()
            observeViewModel()

            Log.d(TAG, "MainActivity onCreate completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in MainActivity onCreate", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
            // Don't crash, just show error
        }
    }
    
    private fun setupToolbar() {
        try {
            setSupportActionBar(binding.toolbar)
            Log.d(TAG, "Toolbar setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up toolbar", e)
        }
    }

    private fun setupRecyclerView() {
        try {
            exerciseAdapter = ExerciseAdapter { exercise ->
                val intent = Intent(this, ExerciseActivity::class.java).apply {
                    putExtra(ExerciseActivity.EXTRA_EXERCISE_ID, exercise.id)
                    putExtra(ExerciseActivity.EXTRA_EXERCISE_NAME, exercise.name)
                }
                startActivity(intent)
            }

            binding.exerciseRecyclerView.apply {
                adapter = exerciseAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            Log.d(TAG, "RecyclerView setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView", e)
        }
    }

    private fun setupFab() {
        try {
            binding.fabHistory.setOnClickListener {
                startActivity(Intent(this, HistoryActivity::class.java))
            }
            Log.d(TAG, "FAB setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up FAB", e)
        }
    }

    private fun observeViewModel() {
        try {
            viewModel.exercises.observe(this) { exercises ->
                Log.d(TAG, "Received ${exercises?.size ?: 0} exercises from ViewModel")
                exerciseAdapter.submitList(exercises)
            }
            Log.d(TAG, "ViewModel observation setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ViewModel observation", e)
        }
    }
}
