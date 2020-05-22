package com.anupkunwar.exercisetimer.model

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ExerciseItem(
    val type: Type,
    val time: Long
) {
    enum class Type(val code: Int) {
        EXERCISE(0) {
            override fun toString(): String {
                return "Exercise"
            }
        },
        ACTIVE_REST(1) {
            override fun toString(): String {
                return "Active Rest"
            }
        },
        REST(2) {
            override fun toString(): String {
                return "Rest"
            }
        }
    }


}

class Converters {
    @TypeConverter
    fun getTypeFromCode(code: Int): ExerciseItem.Type {
        return when (code) {
            0 -> ExerciseItem.Type.EXERCISE
            1 -> ExerciseItem.Type.ACTIVE_REST
            else -> ExerciseItem.Type.REST
        }
    }

    fun setCodeFromType(type: ExerciseItem.Type): Int {
        return type.code
    }
}

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    @Embedded
    val exerciseItems: List<ExerciseItem>
)

@Dao
abstract class ExerciseDao {
    @Insert
    abstract suspend fun insertExercise(exercise: Exercise): Long

    @Query("SELECT * FROM Exercise")
    abstract suspend fun getExercise(): Flow<MutableList<Exercise>>

    suspend fun getDistinctExercise() = getExercise().distinctUntilChanged()
}

@Database(entities = [Exercise::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ExerciseDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseDatabase? = null
        fun getInstance(context: Context): ExerciseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase(context: Context): ExerciseDatabase {
            return Room.databaseBuilder(
                (context.applicationContext),
                ExerciseDatabase::class.java,
                "exercise_db"
            )
                .build()
        }

    }
}





