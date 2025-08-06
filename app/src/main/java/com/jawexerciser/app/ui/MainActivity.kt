package com.jawexerciser.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jawexerciser.app.databinding.ActivityMainBinding
import com.jawexerciser.app.ui.adapter.ExerciseAdapter
import com.jawexerciser.app.ui.exercise.ExerciseActivity
import com.jawexerciser.app.ui.history.HistoryActivity
import com.jawexerciser.app.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var exerciseAdapter: ExerciseAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }
    
    private fun setupRecyclerView() {
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
    }
    
    private fun setupFab() {
        binding.fabHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
    
    private fun observeViewModel() {
        viewModel.exercises.observe(this) { exercises ->
            exerciseAdapter.submitList(exercises)
        }
    }
}
