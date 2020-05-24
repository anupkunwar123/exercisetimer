package com.anupkunwar.exercisetimer.model

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    override fun toString(): String {
        return "$type {$time}sec"
    }


}

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun getTypeFromCode(code: Int): ExerciseItem.Type {
        return when (code) {
            0 -> ExerciseItem.Type.EXERCISE
            1 -> ExerciseItem.Type.ACTIVE_REST
            else -> ExerciseItem.Type.REST
        }
    }

    @TypeConverter
    fun setCodeFromType(type: ExerciseItem.Type): Int {
        return type.code
    }

    @TypeConverter
    fun getListFromJsonString(jsonString: String): List<ExerciseItem> {
        return gson.fromJson(
            jsonString,
            object : TypeToken<List<ExerciseItem>>() {}.type
        )
    }

    @TypeConverter
    fun setJsonStringFromList(list: List<ExerciseItem>): String {
        return gson.toJson(list)
    }
}

@Entity(tableName = "exercise")
data class Exercise constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val exerciseItems: List<ExerciseItem>


) {
    fun getExerciseItemString(): String {
        return buildString {
            for ((i, item) in exerciseItems.withIndex()) {
                append(item)
                if (i < exerciseItems.size - 1) {
                    append(", ")
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise) =
                oldItem.id == newItem.id

        }
    }
}

@Dao
abstract class ExerciseDao {
    @Insert
    abstract suspend fun insertExercise(exercise: Exercise): Long

    @Query("SELECT * FROM exercise WHERE id = :id")
    abstract suspend fun getExerciseById(id: Int): Exercise

    @Query("SELECT * FROM exercise ORDER BY id DESC")
    abstract fun getExercise(): Flow<List<Exercise>>

    fun getDistinctExercise() = getExercise().distinctUntilChanged()
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





