package com.anupkunwar.exercisetimer

import android.os.Bundle
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
import com.anupkunwar.exercisetimer.databinding.FragmentSetListBinding
import com.anupkunwar.exercisetimer.databinding.ItemExerciseSetBinding
import com.anupkunwar.exercisetimer.model.Exercise
import com.anupkunwar.exercisetimer.model.SimpleItemDecoration
import kotlinx.android.synthetic.main.fragment_set_list.*
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * A simple [Fragment] subclass.
 */
class SetListFragment : Fragment() {

    private val setListViewModel: SetListViewModel by viewModels {
        InjectorFactory.getSetListViewModelFactory(requireActivity())
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

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fabCreateSetList.setOnClickListener {
            findNavController().navigate(SetListFragmentDirections.actionSetListFragmentToCreatSetFragment())
        }
        val adapter = SetListAdapter().also {
            it.itemClickListener = object : RecyclerViewItemClickListener<Exercise> {
                override fun onItemClicked(item: Exercise) {
                    findNavController().navigate(
                        SetListFragmentDirections.actionSetListFragmentToTimerFragment2(
                            exerciseItemId = item.id
                        )
                    )
                }

            }
        }
        binding.recyclerView.adapter = adapter
        setListViewModel.savedExercises.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        val simpleItemDecoration = SimpleItemDecoration(
            resources.getDimensionPixelSize(R.dimen.divider_height).toFloat(),
            ContextCompat.getColor(requireContext(), R.color.divider_color)
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
            return SetListViewHolder(binding).also { vh ->
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
}
