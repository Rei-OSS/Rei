package io.github.blackbaroness.rei.common

import java.nio.file.Files
import java.nio.file.Path

class TestHelper {

    companion object {

        fun tempPathFile(): Path {
            return Files.createTempFile(null, null)
        }

        fun tempPathDir(): Path {
            return Files.createTempDirectory(null)
        }
    }
}
