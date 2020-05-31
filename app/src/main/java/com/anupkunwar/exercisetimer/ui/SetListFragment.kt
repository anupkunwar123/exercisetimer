package com.anupkunwar.exercisetimer.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anupkunwar.exercisetimer.*
import com.anupkunwar.exercisetimer.databinding.FragmentSetListBinding
import com.anupkunwar.exercisetimer.databinding.ItemExerciseSetBinding
import com.anupkunwar.exercisetimer.factory.InjectorFactory
import com.anupkunwar.exercisetimer.listener.RecyclerViewItemClickListener
import com.anupkunwar.exercisetimer.model.Exercise
import com.anupkunwar.exercisetimer.model.SimpleItemDecoration
import com.anupkunwar.exercisetimer.service.TimerService
import com.anupkunwar.exercisetimer.viewmodel.SetListViewModel
import kotlinx.android.synthetic.main.fragment_set_list.*
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * A simple [Fragment] subclass.
 */
class SetListFragment : Fragment() {

    private val setListViewModel: SetListViewModel by viewModels {
        InjectorFactory.getSetListViewModelFactory(
            requireActivity()
        )
    }


    private var _binding: FragmentSetListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var mService: TimerService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            mService = binder.getServices()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fabCreateSetList.setOnClickListener {
            findNavController().navigate(SetListFragmentDirections.actionSetListFragmentToCreatSetFragment())
        }
        val adapter = SetListAdapter()
            .also {
            it.itemClickListener = object :
                RecyclerViewItemClickListener<Exercise> {
                override fun onItemClicked(item: Exercise) {
                    if (mBound) {
                        mService.stateLiveData.value =
                            TimerService.State.EXERCISE_SELECTED
                        mService.setName.value = item.title
                        mService.exerciseId = item.id
                        findNavController().navigate(
                            SetListFragmentDirections.actionSetListFragmentToTimerFragment2()
                        )
                    }

                }

            }
        }
        binding.recyclerView.adapter = adapter
        setListViewModel.savedExercises.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        val simpleItemDecoration = SimpleItemDecoration(
            resources.getDimensionPixelSize(R.dimen.divider_height).toFloat(),
            ContextCompat.getColor(requireContext(),
                R.color.divider_color
            )
        )
        binding.recyclerView.addItemDecoration(simpleItemDecoration)
        setListViewModel.getSavedExercise()

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    class SetListAdapter : ListAdapter<Exercise, SetListViewHolder>(Exercise.DIFF_CALLBACK) {
        var itemClickListener: RecyclerViewItemClickListener<Exercise>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetListViewHolder {
            val binding =
                ItemExerciseSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SetListViewHolder(
                binding
            ).also { vh ->
                vh.itemView.setOnClickListener {
                    if (vh.adapterPosition != RecyclerView.NO_POSITION) {
                        itemClickListener?.onItemClicked(getItem(vh.adapterPosition))
                    }
                }
            }
        }

        override fun onBindViewHolder(holder: SetListViewHolder, position: Int) {
            holder.bindItem(getItem(position))
        }

    }

    class SetListViewHolder(private val binding: ItemExerciseSetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(exerciseItem: Exercise) {
            with(binding) {
                item = exerciseItem
                executePendingBindings()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), TimerService::class.java).apply {
            requireContext().bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            requireContext().unbindService(connection)
            mBound = false
        }
    }
}
