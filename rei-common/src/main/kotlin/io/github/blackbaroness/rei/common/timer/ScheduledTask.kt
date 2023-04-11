package io.github.blackbaroness.rei.common.timer

interface ScheduledTask {

    fun cancel()

    val isCancelled: Boolean

    val isDone: Boolean

    val isActive: Boolean
}
