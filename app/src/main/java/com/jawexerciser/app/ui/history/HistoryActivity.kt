package com.jawexerciser.app.ui.history

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jawexerciser.app.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: SessionHistoryAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        historyAdapter = SessionHistoryAdapter()
        binding.historyRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }
    
    private fun observeViewModel() {
        viewModel.sessionsWithExercises.observe(this) { sessions ->
            historyAdapter.submitList(sessions)
        }
        
        viewModel.totalSessions.observe(this) { total ->
            binding.totalSessionsText.text = total.toString()
        }
        
        viewModel.totalReps.observe(this) { total ->
            binding.totalRepsText.text = total.toString()
        }
        
        viewModel.totalTimeMinutes.observe(this) { totalMinutes ->
            binding.totalTimeText.text = "${totalMinutes}m"
        }
    }
}
