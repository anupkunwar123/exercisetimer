package com.anupkunwar.exercisetimer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anupkunwar.exercisetimer.model.Exercise
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

class SetListViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {
    val savedExercises = MutableLiveData<List<Exercise>>()


    @InternalCoroutinesApi
    fun getSavedExercise() {
        viewModelScope.launch {
            exerciseRepository.getExerciseList().collect(object : FlowCollector<List<Exercise>> {
                override suspend fun emit(value: List<Exercise>) {
                    savedExercises.postValue(value)
                }
            })
        }

    }
}