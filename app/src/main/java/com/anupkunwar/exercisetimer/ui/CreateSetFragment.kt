package com.anupkunwar.exercisetimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.anupkunwar.exercisetimer.viewmodel.CreateSetViewModel
import com.anupkunwar.exercisetimer.factory.InjectorFactory
import com.anupkunwar.exercisetimer.R
import com.anupkunwar.exercisetimer.databinding.FragmentCreateSetBinding
import com.anupkunwar.exercisetimer.databinding.ItemExerciseBinding
import com.anupkunwar.exercisetimer.model.ExerciseItem
import com.anupkunwar.exercisetimer.model.SimpleItemDecoration

/**
 * A simple [Fragment] subclass.
 */
class CreateSetFragment : Fragment() {

    private var _binding: FragmentCreateSetBinding? = null
    private val binding get() = _binding!!
    private val createSetViewModel: CreateSetViewModel by viewModels {
        InjectorFactory.getCreateSetViewModelFactory(
            requireActivity()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateSetBinding.inflate(inflater, container, false)
        binding.viewModel = createSetViewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter =
            CreateSetAdapter()
        binding.recyclerView.adapter = adapter
        SimpleItemDecoration(
            resources.getDimensionPixelSize(R.dimen.divider_height).toFloat(),
            ContextCompat.getColor(requireContext(),
                R.color.divider_color
            )
        ).apply {
            binding.recyclerView.addItemDecoration(this)
        }
        createSetViewModel.itemsLiveData.observe(viewLifecycleOwner, Observer {
            adapter.items = it
        })

        createSetViewModel.savedInfo.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().popBackStack()
            }
        })

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    class CreateSetAdapter : RecyclerView.Adapter<CreateSetViewHolder>() {

        var items: List<ExerciseItem>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSetViewHolder {
            return CreateSetViewHolder(
                ItemExerciseBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return items?.size ?: 0
        }

        override fun onBindViewHolder(holder: CreateSetViewHolder, position: Int) {
            holder.bindView(items!![position])
        }

    }

    class CreateSetViewHolder(private val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(i: ExerciseItem) {
            with(binding) {
                item = i
                executePendingBindings()
            }
        }

    }

}
