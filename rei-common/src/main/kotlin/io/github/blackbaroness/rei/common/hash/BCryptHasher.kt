package io.github.blackbaroness.rei.common.hash

import at.favre.lib.crypto.bcrypt.BCrypt
import java.nio.charset.StandardCharsets

class BCryptHasher(private val rounds: Int) {

    fun hash(string: String): String {
        val hash = BCrypt.withDefaults().hash(rounds, string.toByteArray(StandardCharsets.UTF_8))
        return String(hash, StandardCharsets.UTF_8)
    }
}
