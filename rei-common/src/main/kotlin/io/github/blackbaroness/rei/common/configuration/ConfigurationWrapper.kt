package io.github.blackbaroness.rei.common.configuration

interface ConfigurationWrapper<T : Configuration?> {

    fun get(): T

    fun reload()
}
