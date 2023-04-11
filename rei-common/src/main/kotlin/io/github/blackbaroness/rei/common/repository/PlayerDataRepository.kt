package io.github.blackbaroness.rei.common.repository

import io.github.blackbaroness.rei.common.entity.PlayerData
import io.github.blackbaroness.rei.common.repository.exception.RepositoryException
import org.checkerframework.checker.index.qual.NonNegative
import java.util.*

interface PlayerDataRepository {
    @Throws(RepositoryException::class)
    fun openConnection()

    @Throws(RepositoryException::class)
    fun closeConnection()

    fun name(): String?

    @Throws(RepositoryException::class)
    fun create(nickname: String, uuid: UUID): PlayerData

    @Throws(RepositoryException::class)
    fun merge(playerData: PlayerData): PlayerData?

    @Throws(RepositoryException::class)
    fun refresh(playerData: PlayerData)

    @Throws(RepositoryException::class)
    fun delete(playerData: PlayerData)

    @Throws(RepositoryException::class)
    fun ping(): @NonNegative Long

    @Throws(RepositoryException::class)
    fun findByNickname(nickname: String): PlayerData?

    @Throws(RepositoryException::class)
    fun findByUuid(uuid: UUID): PlayerData?

    @Throws(RepositoryException::class)
    fun findAllByUuid(uuid: UUID): Collection<PlayerData>

    @Throws(RepositoryException::class)
    fun findAllByRegistrationDateBetween(from: Date, to: Date): Collection<PlayerData>
}
