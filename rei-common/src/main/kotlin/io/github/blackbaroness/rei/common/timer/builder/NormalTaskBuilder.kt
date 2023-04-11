package io.github.blackbaroness.rei.common.timer.builder

import io.github.blackbaroness.rei.common.timer.ScheduledTask
import org.checkerframework.common.returnsreceiver.qual.This
import java.time.Duration

interface NormalTaskBuilder {

    fun delay(delay: Duration): @This NormalTaskBuilder

    fun daemon(daemon: Boolean): @This NormalTaskBuilder

    fun schedule(runnable: Runnable): ScheduledTask
}
