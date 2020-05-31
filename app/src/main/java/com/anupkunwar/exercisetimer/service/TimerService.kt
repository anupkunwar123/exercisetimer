package com.anupkunwar.exercisetimer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.anupkunwar.exercisetimer.factory.InjectorFactory
import com.anupkunwar.exercisetimer.MainActivity
import com.anupkunwar.exercisetimer.MyApp
import com.anupkunwar.exercisetimer.R
import com.anupkunwar.exercisetimer.repo.ExerciseRepository
import kotlinx.coroutines.*

class TimerService : Service() {

    companion object {
        const val START_FOREGROUND = 0
        const val STOP_FOREGROUND = 1
        const val PAUSE_TIMER = 2
        const val PLAY_TIMER = 3
        const val TIMER_SERVICE_DATA = "timerServiceData"
    }

    private val scope = MainScope()
    var exerciseId = -1
    val timeLiveData = MutableLiveData<Int>()
    val setName = MutableLiveData<String>()
    val exerciseName = MutableLiveData<String>()
    var pausePlayLiveData = MutableLiveData<Boolean>()
    var stateLiveData = MutableLiveData<State>()
    var totalTimeLiveData = MutableLiveData<Int>()
    var showNotification = true
    private var job: Job? = null

    enum class State {
        NONE,
        EXERCISE_SELECTED,
        EXERCISE_RUNNING
    }

    private lateinit var repository: ExerciseRepository
    private val binder = LocalBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        repository =
            InjectorFactory.getExerciseRepository(
                this
            )
        totalTimeLiveData.observeForever(observer)
    }


    private fun buildNotification(): Notification {

        return NotificationCompat.Builder(this,
            MyApp.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(setName.value)
            .setContentIntent(getPendingIntent())
            .setContentText(
                String.format(
                    getString(R.string.notification_minute_second),
                    exerciseName.value, timeLiveData.value,
                    totalTimeLiveData.value!! / 60,
                    totalTimeLiveData.value!! % 60
                )
            )
            .addAction(
                if (isPaused()) R.drawable.ic_play else R.drawable.ic_pause,
                if (isPaused()) getString(R.string.play) else getString(
                    R.string.pause
                ),
                getPendingIntentPausePlay()
            ).build()
    }

    private fun getPendingIntent(): PendingIntent {
        val notificationIntent = Intent(this, MainActivity::class.java)

        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        return PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
    }

    private fun getPendingIntentPausePlay(): PendingIntent {
        val notificationIntent = Intent(this, TimerService::class.java)
        notificationIntent.putExtra(TIMER_SERVICE_DATA, if (isPaused()) PLAY_TIMER else PAUSE_TIMER)
        return PendingIntent.getService(
            this, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    private val observer = Observer<Int> {
        if (showNotification) {
            with(NotificationManagerCompat.from(this)) {
                notify(1, buildNotification())
            }
        } else {
            with(NotificationManagerCompat.from(this)) {
                cancel(1)
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { i ->
            when (i.getIntExtra(TIMER_SERVICE_DATA, -1)) {
                START_FOREGROUND -> startForeground(1, buildNotification())
                STOP_FOREGROUND -> stopForeground(true)
                PAUSE_TIMER -> {
                    pauseTimer()
                    with(NotificationManagerCompat.from(this)) {
                        notify(1, buildNotification())
                    }
                }
                PLAY_TIMER -> {
                    playTimer()
                    with(NotificationManagerCompat.from(this)) {
                        notify(1, buildNotification())
                    }
                }
                else -> {
                    throw  IllegalArgumentException("Shouldn't get here")
                }
            }
        }

        return START_NOT_STICKY
    }


    fun startTimer() {
        if (exerciseId == -1) return
        job = scope.launch(Dispatchers.IO) {
            pausePlayLiveData.postValue(false)
            stateLiveData.postValue(State.EXERCISE_RUNNING)
            val exercise = repository.getExerciseById(exerciseId)
            setName.postValue(exercise.title)
            var i = 0
            while (i < exercise.exerciseItems.size && isActive) {
                var time = exercise.exerciseItems[i].time
                exerciseName.postValue(exercise.exerciseItems[i].type.toString())
                while (time >= 1 && isActive) {
                    if (!pausePlayLiveData.value!!) {
                        timeLiveData.postValue(time)
                        totalTimeLiveData.postValue(if (totalTimeLiveData.value == null) 0 else totalTimeLiveData.value!! + 1)
                        Thread.sleep(1000)
                        time--
                    } else {
                        Thread.sleep(1000)
                    }
                }
                i++
                if (i >= exercise.exerciseItems.size) {
                    i = 0
                }
            }

        }
    }


    fun pauseTimer() {
        if (stateLiveData.value == State.EXERCISE_RUNNING) {
            pausePlayLiveData.value = true
        }
    }

    fun playTimer() {
        if (stateLiveData.value == State.EXERCISE_RUNNING) {
            pausePlayLiveData.value = false
        }
    }

    fun isPaused(): Boolean {
        return pausePlayLiveData.value ?: false
    }

    fun reset() {
        job?.cancel()
        timeLiveData.value = null
        setName.value = ""
        exerciseName.value = ""
        pausePlayLiveData.value = false
        stateLiveData.value =
            State.NONE
        totalTimeLiveData.value = null
    }

    override fun onDestroy() {
        timeLiveData.removeObserver(observer)
        scope.cancel()
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        fun getServices() = this@TimerService
    }
}