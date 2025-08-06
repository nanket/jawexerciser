package com.jawexerciser.app.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jawexerciser.app.data.model.Exercise
import com.jawexerciser.app.data.model.Session
import com.jawexerciser.app.databinding.ItemSessionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

data class SessionWithExercise(
    val session: Session,
    val exerciseName: String
)

class SessionHistoryAdapter : ListAdapter<SessionWithExercise, SessionHistoryAdapter.SessionViewHolder>(
    SessionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SessionViewHolder(
        private val binding: ItemSessionHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        fun bind(sessionWithExercise: SessionWithExercise) {
            val session = sessionWithExercise.session
            
            binding.exerciseNameText.text = sessionWithExercise.exerciseName
            binding.sessionDateText.text = dateFormat.format(session.startTime)
            binding.repsText.text = session.totalReps.toString()
            
            val minutes = session.durationSeconds / 60
            val seconds = session.durationSeconds % 60
            binding.durationText.text = String.format("%d:%02d", minutes, seconds)
        }
    }

    private class SessionDiffCallback : DiffUtil.ItemCallback<SessionWithExercise>() {
        override fun areItemsTheSame(
            oldItem: SessionWithExercise,
            newItem: SessionWithExercise
        ): Boolean {
            return oldItem.session.id == newItem.session.id
        }

        override fun areContentsTheSame(
            oldItem: SessionWithExercise,
            newItem: SessionWithExercise
        ): Boolean {
            return oldItem == newItem
        }
    }
}
