package com.jawexerciser.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val iconResource: String? = null
) {
    companion object {
        const val JAW_OPENER_ID = 1
        const val CHIN_LIFT_ID = 2
        const val SIDE_TO_SIDE_ID = 3
        
        fun getDefaultExercises(): List<Exercise> {
            return listOf(
                Exercise(
                    id = JAW_OPENER_ID,
                    name = "Jaw Opener",
                    description = "Open your mouth wide and hold for 2 seconds, then close slowly"
                ),
                Exercise(
                    id = CHIN_LIFT_ID,
                    name = "Chin Lift",
                    description = "Lift your chin up while keeping your mouth closed"
                ),
                Exercise(
                    id = SIDE_TO_SIDE_ID,
                    name = "Side-to-Side Jaw Shift",
                    description = "Move your jaw from side to side in a controlled motion"
                )
            )
        }
    }
}
