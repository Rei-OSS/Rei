package io.github.blackbaroness.rei.common.hash

import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.inject.Inject
import com.google.inject.Singleton
import io.github.blackbaroness.rei.common.configuration.Configuration
import io.github.blackbaroness.rei.common.configuration.ConfigurationWrapper
import io.github.blackbaroness.rei.common.timer.impl.AsyncTaskScheduler
import io.github.blackbaroness.rei.common.util.RandomUtil
import org.slf4j.Logger
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit

@Singleton
class HashingService @Inject constructor(
    private val configuration: ConfigurationWrapper<Configuration>,
    private val asyncTaskScheduler: AsyncTaskScheduler,
    private val logger: Logger
) {
    private var hasher: BCryptHasher? = null

    init {
        adjustCost()
    }

    fun hash(string: String): String {
        return hasher!!.hash(string)
    }

    fun isValid(hash: String, suspected: String): Boolean {
        return BCrypt.verifyer().verify(
            hash.toByteArray(StandardCharsets.UTF_8),
            suspected.toByteArray(StandardCharsets.UTF_8)
        ).verified
    }

    private fun adjustCost() {
        val hashing = configuration.get().passwords().hashing()
        if (hashing.bcryptRounds() != 0) {
            // Auto adjusting disabled.
            hasher = BCryptHasher(hashing.bcryptRounds())
            return
        }

        // Temporal solution. While we are producing a benchmark, players must register somehow.
        hasher = BCryptHasher(12)
        runBenchmark()
    }

    private fun runBenchmark() {
        asyncTaskScheduler.newTask().schedule {
            val rounds = findOptimalBcryptRounds()
            logger.info("Bcrypt cost adjusted to $rounds")
            if (rounds < 12) {
                logger.warn(
                    "This is a pretty bad result. " +
                        "If you want to increase security, you need to increase server performance."
                )
            }

            //TODO IMPLEMENT ME
        }
    }

    private fun findOptimalBcryptRounds(): Int {
        for (rounds in BCrypt.MIN_COST until BCrypt.MAX_COST) {
            val examplePassword: String = RandomUtil.nextStringSecure(12)
            val timestamp = Instant.now()
            BCrypt.withDefaults().hash(rounds, examplePassword.toByteArray(StandardCharsets.UTF_8))
            if (timestamp.until(Instant.now(), ChronoUnit.MINUTES) >= 1) {
                return rounds
            }
        }
        return BCrypt.MAX_COST
    }
}
