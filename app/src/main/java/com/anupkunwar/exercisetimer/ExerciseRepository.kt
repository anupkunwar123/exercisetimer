package com.anupkunwar.exercisetimer

import com.anupkunwar.exercisetimer.model.Exercise
import com.anupkunwar.exercisetimer.model.ExerciseDatabase
import kotlinx.coroutines.flow.Flow

class ExerciseRepository private constructor(private val exerciseDatabase: ExerciseDatabase) {
    suspend fun insert(exercise: Exercise): Long {
        return exerciseDatabase.exerciseDao().insertExercise(exercise)
    }

    fun getExerciseList(): Flow<List<Exercise>> {
        return exerciseDatabase.exerciseDao().getDistinctExercise()
    }

    suspend fun getExerciseById(id: Int): Exercise {
        return exerciseDatabase.exerciseDao().getExerciseById(id)
    }

    companion object {
        @Volatile
        private var instance: ExerciseRepository? = null
        fun getInstance(exerciseDatabase: ExerciseDatabase): ExerciseRepository {
            return instance ?: synchronized(this) {
                instance ?: ExerciseRepository(exerciseDatabase).also {
                    instance = it
                }
            }
        }
    }
}