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
        // todo cache #verifyer call?
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
            hasher = BCryptHasher(rounds)

            val resultElevation = when {
                rounds > 12 -> """
                        <green>This is good result because it is higher than 12.
                        <green>Don't worry about it - Rei will do the job.
                    """.trimIndent()

                else -> """
                        <red>This is pretty bad result because it is lower than 12.
                        <yellow>This does`t means your server will be hacked but, if your database will
                        <yellow>be leaked, a hacker will be more likely to get one of the passwords using brute force.

                        <red> Solutions:
                        <yellow> 1. Increase your server performance. Most likely this will require CPU upgrade.
                        <yellow> 2. Set fixed bcrypt rounds inside config. This will make players wait longer to register
                        <yellow>    (there will be silence after entering the command), but the load on the server should not increase.
                    """.trimIndent()
            }

            logger.info(
                """
                Automatic BCrypt cost adjustment done with result $rounds
                This means now all new password will be stored with optimal security,
                depending on your server performance.

                $resultElevation
            """.trimIndent()
            )
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
