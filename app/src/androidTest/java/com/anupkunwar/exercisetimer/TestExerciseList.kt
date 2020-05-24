package com.anupkunwar.exercisetimer

import com.anupkunwar.exercisetimer.model.Exercise
import com.anupkunwar.exercisetimer.model.ExerciseItem

object TestExerciseList {
    val TEST_EXERCISE_DATA = arrayListOf(
        Exercise(
            title = "Hit Set", exerciseItems = listOf(
                ExerciseItem(type = ExerciseItem.Type.EXERCISE, time = 60)
            )
        ),
        Exercise(
            title = "Aerobics", exerciseItems = listOf(
                ExerciseItem(type = ExerciseItem.Type.EXERCISE, time = 60),
                ExerciseItem(type = ExerciseItem.Type.REST, time = 30)
            )
        )

    )
}