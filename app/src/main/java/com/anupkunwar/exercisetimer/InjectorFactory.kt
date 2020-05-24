package com.anupkunwar.exercisetimer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anupkunwar.exercisetimer.model.ExerciseDatabase

object InjectorFactory {
    fun getCreateSetViewModelFactory(context: Context): CreateSetViewModelFactory {
        return CreateSetViewModelFactory(context)
    }

    fun getSetListViewModelFactory(context: Context): SetListViewModelFactory {
        return SetListViewModelFactory(context)
    }

    fun getDatabase(context: Context): ExerciseDatabase {
        return ExerciseDatabase.getInstance(context)
    }

    fun getExerciseRepository(context: Context): ExerciseRepository {
        return ExerciseRepository.getInstance(getDatabase(context))
    }


}

class CreateSetViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateSetViewModel(InjectorFactory.getExerciseRepository(context)) as T
    }
}

class SetListViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SetListViewModel(InjectorFactory.getExerciseRepository(context)) as T
    }
}

