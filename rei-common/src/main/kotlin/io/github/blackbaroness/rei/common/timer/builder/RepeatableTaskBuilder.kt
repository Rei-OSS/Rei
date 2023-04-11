package io.github.blackbaroness.rei.common.timer.builder

import io.github.blackbaroness.rei.common.timer.ScheduledTask
import org.checkerframework.common.returnsreceiver.qual.This
import java.time.Duration

interface RepeatableTaskBuilder {

    fun delay(delay: Duration): @This RepeatableTaskBuilder

    fun period(period: Duration): @This RepeatableTaskBuilder

    fun daemon(daemon: Boolean): @This RepeatableTaskBuilder

    fun schedule(runnable: Runnable): ScheduledTask
}
