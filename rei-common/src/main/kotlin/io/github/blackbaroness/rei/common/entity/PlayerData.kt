package io.github.blackbaroness.rei.common.entity

import java.time.Instant
import java.util.*

interface PlayerData {

    fun nickname(): String

    fun uuid(): UUID

    fun registrationDate(): Instant?

    fun registrationDate(registrationDate: Instant?)

    fun password(): String?

    fun password(password: String?)
}
