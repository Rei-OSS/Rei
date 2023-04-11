package io.github.blackbaroness.rei.common.timer

import io.github.blackbaroness.rei.common.timer.builder.NormalTaskBuilder
import io.github.blackbaroness.rei.common.timer.builder.RepeatableTaskBuilder
import org.jetbrains.annotations.Unmodifiable

interface TaskScheduler {

    fun newTask(): NormalTaskBuilder

    fun newRepeatableTask(): RepeatableTaskBuilder

    fun activeNormalTasks(): @Unmodifiable Collection<ScheduledTask>

    fun activeRepeatableTasks(): @Unmodifiable Collection<ScheduledTask>

    fun cancelNormalTasks(): Long

    fun cancelRepeatableTasks(): Long
}
