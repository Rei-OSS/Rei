package io.github.blackbaroness.rei.common.configuration

import io.github.blackbaroness.rei.common.TestHelper
import org.junit.jupiter.api.Test
import space.arim.dazzleconf.ConfigurationOptions
import space.arim.dazzleconf.ext.snakeyaml.CommentMode
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions
import space.arim.dazzleconf.helper.ConfigurationHelper

class ConfigurationCompileTest {

    @Test
    fun check_is_configuration_class_can_be_parsed() {
        val tempFile = TestHelper.tempPathFile()
        val dir = tempFile.parent
        val fileName = tempFile.fileName.toString()

        ConfigurationHelper(
            dir, fileName, SnakeYamlConfigurationFactory.create(
                Configuration::class.java,
                ConfigurationOptions.defaults(),
                SnakeYamlOptions.Builder()
                    .commentMode(CommentMode.fullComments())
                    .build()
            )
        ).reloadConfigData()
    }
}
