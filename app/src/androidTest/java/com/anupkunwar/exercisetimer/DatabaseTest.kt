package com.anupkunwar.exercisetimer

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anupkunwar.exercisetimer.model.Exercise
import com.anupkunwar.exercisetimer.model.ExerciseDao
import com.anupkunwar.exercisetimer.model.ExerciseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: ExerciseDatabase
    private lateinit var dao: ExerciseDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExerciseDatabase::class.java
        ).build()
        dao = db.exerciseDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testInsert() {
        runBlocking {
            dao.insertExercise(TestExerciseList.TEST_EXERCISE_DATA[0])
            assert(dao.getExercise().count() == 1)
        }
    }

    @InternalCoroutinesApi
    @Test
    fun testQuery(){
        runBlocking {
            dao.insertExercise(TestExerciseList.TEST_EXERCISE_DATA[1])
            dao.getDistinctExercise().collect(object : FlowCollector<List<Exercise>> {
                override suspend fun emit(value: List<Exercise>) {
                    assert(value[0].title == TestExerciseList.TEST_EXERCISE_DATA[1].title)
                }
            })
        }
    }
}