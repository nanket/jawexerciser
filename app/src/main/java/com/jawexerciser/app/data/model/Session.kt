package com.jawexerciser.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: Int,
    val startTime: Date,
    val endTime: Date?,
    val totalReps: Int = 0,
    val durationSeconds: Long = 0
)
