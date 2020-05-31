package com.anupkunwar.exercisetimer.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anupkunwar.exercisetimer.model.ExerciseDatabase
import com.anupkunwar.exercisetimer.repo.ExerciseRepository
import com.anupkunwar.exercisetimer.viewmodel.CreateSetViewModel
import com.anupkunwar.exercisetimer.viewmodel.SetListViewModel

object InjectorFactory {
    fun getCreateSetViewModelFactory(context: Context): CreateSetViewModelFactory {
        return CreateSetViewModelFactory(
            context = context
        )
    }

    fun getSetListViewModelFactory(context: Context): SetListViewModelFactory {
        return SetListViewModelFactory(
            context = context
        )
    }

    private fun getDatabase(context: Context): ExerciseDatabase {
        return ExerciseDatabase.getInstance(context = context)
    }


    fun getExerciseRepository(context: Context): ExerciseRepository {
        return ExerciseRepository.getInstance(
            getDatabase(
                context = context
            )
        )
    }


}

@Suppress("UNCHECKED_CAST")
class CreateSetViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateSetViewModel(
            InjectorFactory.getExerciseRepository(
                context
            )
        ) as T
    }
}

@Suppress("UNCHECKED_CAST")
class SetListViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SetListViewModel(
            InjectorFactory.getExerciseRepository(
                context
            )
        ) as T
    }
}





