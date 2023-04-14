package io.github.blackbaroness.rei.common.timer.impl

import com.google.inject.Singleton
import io.github.blackbaroness.fastutilextender.common.set.FastSet
import io.github.blackbaroness.rei.common.timer.ScheduledTask
import io.github.blackbaroness.rei.common.timer.TaskScheduler
import io.github.blackbaroness.rei.common.timer.builder.NormalTaskBuilder
import io.github.blackbaroness.rei.common.timer.builder.RepeatableTaskBuilder
import org.checkerframework.common.returnsreceiver.qual.This
import java.lang.reflect.Field
import java.time.Duration
import java.util.*
import javax.annotation.concurrent.NotThreadSafe
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
@Singleton
class AsyncTaskScheduler : TaskScheduler {

    private val nonDaemonTimer: Timer by lazy { Timer() }
    private val daemonTimer: Timer by lazy { Timer(true) }
    private val activeNormalTasks: MutableSet<ScheduledTask> = FastSet.objects.create()
    private val activeRepeatableTasks: MutableSet<ScheduledTask> = FastSet.objects.create()

    override fun newTask(): AsyncNormalTaskBuilder {
        return AsyncNormalTaskBuilder()
    }

    override fun newRepeatableTask(): AsyncRepeatableTaskBuilder {
        return AsyncRepeatableTaskBuilder()
    }

    override fun activeNormalTasks(): Collection<ScheduledTask> {
        synchronized(activeNormalTasks) {
            return getActiveTasks(activeNormalTasks)
        }
    }

    override fun activeRepeatableTasks(): Collection<ScheduledTask> {
        synchronized(activeRepeatableTasks) {
            return getActiveTasks(activeRepeatableTasks)
        }
    }

    override fun cancelNormalTasks(): Long {
        synchronized(activeNormalTasks) {
            return cancelTasks(activeNormalTasks)
        }
    }

    override fun cancelRepeatableTasks(): Long {
        synchronized(activeRepeatableTasks) {
            return cancelTasks(activeNormalTasks)
        }
    }

    private fun cancelTasks(tasks: MutableCollection<ScheduledTask>): Long {
        removeInactiveTasks(tasks)
        val tasksCancelled = tasks.size
        tasks.forEach { task -> task.cancel() }
        return tasksCancelled.toLong()
    }

    private fun getActiveTasks(tasks: MutableCollection<ScheduledTask>): Collection<ScheduledTask> {
        removeInactiveTasks(tasks)
        return FastSet.objects.builder<ScheduledTask>().content(tasks).unmodifiable().build()
    }

    private fun timerFor(daemon: Boolean): Timer {
        return if (daemon) daemonTimer else nonDaemonTimer
    }

    private fun removeInactiveTasks(tasks: MutableCollection<ScheduledTask>) {
        tasks.removeIf { task -> !task.isActive }
    }

    class AsyncScheduledTask(timerTask: TimerTask) : ScheduledTask {

        private val timerTask: TimerTask

        init {
            this.timerTask = timerTask
        }

        override fun cancel() {
            timerTask.cancel()
        }

        override val isCancelled: Boolean
            get() = state() == TimerTaskState.CANCELLED

        override val isDone: Boolean
            get() = state() == TimerTaskState.EXECUTED

        override val isActive: Boolean
            get() = state() == TimerTaskState.VIRGIN || state() == TimerTaskState.SCHEDULED

        private fun state(): TimerTaskState {
            return try {
                val state: Field = timerTask.javaClass.getField("state")
                state.isAccessible = true
                when (val statusCode = state.getInt(timerTask)) {
                    0 -> TimerTaskState.VIRGIN
                    1 -> TimerTaskState.SCHEDULED
                    2 -> TimerTaskState.EXECUTED
                    3 -> TimerTaskState.CANCELLED
                    else -> throw IllegalStateException("Unexpected value: $statusCode")
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    @NotThreadSafe
    inner class AsyncNormalTaskBuilder : NormalTaskBuilder {

        private var delay = Duration.ZERO
        private var daemon = true

        override fun delay(delay: Duration): @This AsyncNormalTaskBuilder {
            this.delay = delay
            return this
        }

        override fun daemon(daemon: Boolean): @This AsyncNormalTaskBuilder {
            this.daemon = daemon
            return this
        }

        override fun schedule(runnable: Runnable): AsyncScheduledTask {
            return schedule(object : TimerTask() {
                override fun run() {
                    runnable.run()
                }
            })
        }

        fun schedule(timerTask: TimerTask): AsyncScheduledTask {
            val task = AsyncScheduledTask(timerTask)
            synchronized(activeNormalTasks) { activeNormalTasks.add(task) }
            timerFor(daemon).schedule(timerTask, delay.toMillis())
            return task
        }
    }

    @NotThreadSafe
    inner class AsyncRepeatableTaskBuilder : RepeatableTaskBuilder {

        private var delay = Duration.ZERO
        private var period = Duration.ZERO
        private var daemon = true

        override fun delay(delay: Duration): @This AsyncRepeatableTaskBuilder {
            this.delay = delay
            return this
        }

        override fun period(period: Duration): @This AsyncRepeatableTaskBuilder {
            this.period = period
            return this
        }

        override fun daemon(daemon: Boolean): @This AsyncRepeatableTaskBuilder {
            this.daemon = daemon
            return this
        }

        override fun schedule(runnable: Runnable): AsyncScheduledTask {
            return schedule(object : TimerTask() {
                override fun run() {
                    runnable.run()
                }
            })
        }

        fun schedule(timerTask: TimerTask): AsyncScheduledTask {
            val task = AsyncScheduledTask(timerTask)
            synchronized(activeRepeatableTasks) { activeRepeatableTasks.add(task) }
            timerFor(daemon).scheduleAtFixedRate(timerTask, delay.toMillis(), period.toMillis())
            return task
        }
    }

    enum class TimerTaskState {
        // 1
        VIRGIN,

        // 2
        SCHEDULED,

        // 3
        EXECUTED,

        // 4
        CANCELLED
    }
}
