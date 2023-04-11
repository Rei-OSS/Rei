package io.github.blackbaroness.rei.common.entity

import org.checkerframework.checker.i18n.qual.LocalizableKey
import java.util.*

interface Player {

    fun online(): Boolean

    fun nick(): String

    fun uuid(): UUID

    fun locale(): Locale

    fun sendMessage(message: String)

    fun sendMessageRaw(message: String)

    fun sendMessageLocalized(key: @LocalizableKey String, vararg args: Any)

    fun kick(reason: String)
}
