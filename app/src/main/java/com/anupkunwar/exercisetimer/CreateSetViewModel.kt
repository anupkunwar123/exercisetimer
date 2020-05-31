package com.anupkunwar.exercisetimer

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anupkunwar.exercisetimer.model.Exercise
import com.anupkunwar.exercisetimer.model.ExerciseItem
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

class CreateSetViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {
    companion object {
        const val DEFAULT_EXERCISE_TIME = 60
        const val DEFAULT_ACTIVE_REST_TIME = 30
        const val DEFAULT_REST_TIME = 20
    }

    val savedInfo = MutableLiveData<Boolean>()

    var exerciseTimeInSec = DEFAULT_EXERCISE_TIME
    var activeRestTimeInSec = DEFAULT_ACTIVE_REST_TIME
    var restTimeInSec = DEFAULT_REST_TIME

    var setName = ObservableField("")

    private val exerciseItem = mutableListOf<ExerciseItem>()
    val itemsLiveData = MutableLiveData<List<ExerciseItem>>()

    fun addExercise() {
        exerciseItem.add(ExerciseItem(type = ExerciseItem.Type.EXERCISE, time = exerciseTimeInSec))
        itemsLiveData.value = exerciseItem
    }

    fun addActiveRest() {
        exerciseItem.add(
            ExerciseItem(
                type = ExerciseItem.Type.ACTIVE_REST,
                time = activeRestTimeInSec
            )
        )
        itemsLiveData.value = exerciseItem
    }

    fun addRest() {
        exerciseItem.add(ExerciseItem(type = ExerciseItem.Type.REST, time = restTimeInSec))
        itemsLiveData.value = exerciseItem
    }

    fun saveExercise() {
        if (setName.get().isNullOrBlank()) return
        if (itemsLiveData.value.isNullOrEmpty()) return
        viewModelScope.launch {
            exerciseRepository.insert(
                Exercise(
                    title = setName.get()!!,
                    exerciseItems = itemsLiveData.value!!
                )
            ).also {
                if (it > 0) {
                    savedInfo.value = true
                }
            }
        }
    }




}