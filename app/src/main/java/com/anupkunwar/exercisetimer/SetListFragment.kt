package com.anupkunwar.exercisetimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_set_list.*

/**
 * A simple [Fragment] subclass.
 */
class SetListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fabCreateSetList.setOnClickListener {
            findNavController().navigate(SetListFragmentDirections.actionSetListFragmentToCreatSetFragment())
        }
    }

}
