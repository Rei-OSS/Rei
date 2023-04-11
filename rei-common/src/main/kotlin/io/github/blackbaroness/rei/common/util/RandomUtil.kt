package io.github.blackbaroness.rei.common.util

import com.fasterxml.uuid.Generators
import com.google.common.base.Preconditions
import org.checkerframework.checker.index.qual.Positive
import org.jetbrains.annotations.Range
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Утилита для быстрой работы со случайными значениями.
 * Работает быстрее, чем обычный [java.util.Random]
 */

class RandomUtil {

    companion object {

        private val NORMAL_SYMBOLS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz".toCharArray()
        private val NORMAL_SYMBOLS_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        private val NORMAL_SYMBOLS_NUMBERS = "0123456789".toCharArray()
        private val NORMAL_SYMBOLS_ALL: CharArray =
            ArrayUtil.merge(NORMAL_SYMBOLS_LOWERCASE, NORMAL_SYMBOLS_UPPERCASE, NORMAL_SYMBOLS_NUMBERS)
        private val SYMBOLS_SPECIAL = "^$?!@#%&".toCharArray()
        private val SYMBOLS_ALL: CharArray = ArrayUtil.merge(NORMAL_SYMBOLS_ALL, SYMBOLS_SPECIAL)

        @JvmStatic
        fun fastEngine(): ThreadLocalRandom {
            return ThreadLocalRandom.current()
        }

        @JvmStatic
        fun nextUUIDv1(): UUID {
            return Generators.timeBasedGenerator().generate()
        }

        @JvmStatic
        fun nextUUIDv4(): UUID {
            return Generators.randomBasedGenerator(fastEngine()).generate()
        }

        @JvmStatic
        fun nextUUID(): UUID {
            return nextUUIDv4()
        }

        @JvmStatic
        fun nextString(length: @Positive Int): String {
            Preconditions.checkArgument(length >= 0, "length must be positive")
            val builder = StringBuilder()
            for (i in 0 until length) {
                builder.append(nextElement(NORMAL_SYMBOLS_ALL))
            }
            return builder.toString()
        }

        @JvmStatic
        fun nextStringSecure(length: @Range(from = 6, to = Int.MAX_VALUE.toLong()) Int): String {
            Preconditions.checkArgument(length >= 6, "length must be 6 or greater")
            val builder = StringBuilder()
            for (i in 0 until length - 4) {
                builder.append(nextElement(SYMBOLS_ALL))
            }
            builder.insert(fastEngine().nextInt(builder.length), nextElement(NORMAL_SYMBOLS_LOWERCASE))
            builder.insert(fastEngine().nextInt(builder.length), nextElement(NORMAL_SYMBOLS_UPPERCASE))
            builder.insert(fastEngine().nextInt(builder.length), nextElement(NORMAL_SYMBOLS_NUMBERS))
            builder.insert(fastEngine().nextInt(builder.length), nextElement(SYMBOLS_SPECIAL))
            return builder.toString()
        }

        @JvmStatic
        fun <T> nextElement(array: Array<T>): T {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun nextElement(array: CharArray): Char {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun nextElement(array: IntArray): Int {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun nextElement(array: DoubleArray): Double {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun nextElement(array: FloatArray): Float {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun nextElement(array: ByteArray): Byte {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun nextElement(array: BooleanArray): Boolean {
            return array[fastEngine().nextInt(array.size)]
        }

        @JvmStatic
        fun <T> nextElement(list: List<T>): T {
            return list[fastEngine().nextInt(list.size)]
        }
    }
}
