package com.jawexerciser.app.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jawexerciser.app.data.dao.ExerciseDao
import com.jawexerciser.app.data.dao.RepDao
import com.jawexerciser.app.data.dao.SessionDao
import com.jawexerciser.app.data.model.Exercise
import com.jawexerciser.app.data.model.Rep
import com.jawexerciser.app.data.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Exercise::class, Session::class, Rep::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JawExerciserDatabase : RoomDatabase() {
    
    abstract fun exerciseDao(): ExerciseDao
    abstract fun sessionDao(): SessionDao
    abstract fun repDao(): RepDao
    
    companion object {
        @Volatile
        private var INSTANCE: JawExerciserDatabase? = null
        
        fun getDatabase(context: Context): JawExerciserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JawExerciserDatabase::class.java,
                    "jaw_exerciser_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.exerciseDao())
                    }
                }
            }
        }
        
        private suspend fun populateDatabase(exerciseDao: ExerciseDao) {
            exerciseDao.insertExercises(Exercise.getDefaultExercises())
        }
    }
}
