package io.github.blackbaroness.rei.common.util

import com.google.common.base.Preconditions

class ArrayUtil {

    companion object {

        fun <T> merge(vararg arrays: Array<T>): Array<T> {
            Preconditions.checkArgument(arrays.isNotEmpty(), "You must provide at least 1 array")
            if (arrays.size == 1) return arrays[0]
            var first = arrays[0]
            for (i in 1 until arrays.size) {
                first = first.plus(arrays[i])
            }
            return first
        }

        fun merge(vararg arrays: CharArray): CharArray {
            Preconditions.checkArgument(arrays.isNotEmpty(), "You must provide at least 1 array")
            if (arrays.size == 1) return arrays[0]
            var first = arrays[0]
            for (i in 1 until arrays.size) {
                first = first.plus(arrays[i])
            }
            return first
        }


    }
}
