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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.anupkunwar.exercisetimer.R
import com.anupkunwar.exercisetimer.service.TimerService
import com.anupkunwar.exercisetimer.databinding.FragmentTimerBinding

/**
 * A simple [Fragment] subclass.
 */
class TimerFragment : Fragment() {

    private lateinit var mService: TimerService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            mBound = true
            mService = binder.getServices()
            mService.timeLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                binding.timerTextView.text = it.toString()
            })
            mService.totalTimeLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                val minute = it / 60
                val second = it % 60
                binding.textViewTotalTime.text =
                    String.format(getString(R.string.minute_second), minute, second)
            })
            mService.exerciseName.observe(viewLifecycleOwner, Observer {
                binding.textViewSetName.text = it
            })
            mService.setName.observe(viewLifecycleOwner, Observer {
                binding.textViewTitle.text = it


            })
            mService.showNotification = false
            requireContext().startService(Intent(requireContext(), TimerService::class.java).also {
                it.putExtra(
                    TimerService.TIMER_SERVICE_DATA,
                    TimerService.STOP_FOREGROUND
                )
            })
            mService.pausePlayLiveData.observe(viewLifecycleOwner, Observer { isPaused ->
                binding.buttonPausePlay.setImageResource(if (isPaused) R.drawable.ic_play else R.drawable.ic_pause)
                binding.buttonPausePlay.isEnabled = true
                binding.buttonReset.isGone = !isPaused
            })

            binding.stateLiveData = mService.stateLiveData

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonPausePlay.setOnClickListener {
            if (mBound && mService.isPaused()) {
                it.isEnabled = false
                mService.playTimer()
                return@setOnClickListener
            }
            if (mBound && !mService.isPaused()) {
                it.isEnabled = false
                mService.pauseTimer()
            }
        }


        binding.buttonStart.setOnClickListener {
            if (mBound) {
                mService.startTimer()
            }
        }
        binding.buttonReset.setOnClickListener {
            if (mBound) {
                mService.reset()
            }
        }




        binding.buttonSelectExerciseSet.setOnClickListener {
            findNavController().navigate(TimerFragmentDirections.actionTimerFragmentToSetListFragment())
        }


    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), TimerService::class.java).apply {
            requireContext().bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBound && mService.stateLiveData.value == TimerService.State.EXERCISE_RUNNING) {
            requireContext().startService(Intent(requireContext(), TimerService::class.java).also {
                it.putExtra(
                    TimerService.TIMER_SERVICE_DATA,
                    TimerService.START_FOREGROUND
                )
            })
        }

    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            mService.showNotification = true
            requireContext().unbindService(connection)
            mBound = false
        }
    }


}
